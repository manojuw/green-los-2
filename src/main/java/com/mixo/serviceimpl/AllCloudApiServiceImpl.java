package com.mixo.serviceimpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mixo.dto.LoanStatus;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerBank;
import com.mixo.model.BorrowerCibil;
import com.mixo.model.Customer;
import com.mixo.model.EmiBreakUp;
import com.mixo.model.Product;
import com.mixo.model.RuleEngine;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerBankRepository;
import com.mixo.repository.BorrowerCibilRepository;
import com.mixo.repository.BorrowerDocRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.repository.CustomerRepository;
import com.mixo.repository.EmiBreakUpRepository;
import com.mixo.service.AllCloudApiService;
import com.mixo.service.ApiService;
import com.mixo.service.AwsS3Service;
import com.mixo.service.ProductService;
import com.mixo.service.RuleEngineService;
import com.mixo.utils.ResponseCodeEnum;
import com.mixo.utils.ValidatedRequest;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AllCloudApiServiceImpl implements AllCloudApiService {

	@Autowired
	private WebClient webClient;

	@Value("${appid}")
	private String appid;
	@Value("${secrettoken}")
	private String secrettoken;
	@Value("${usertoken}")
	private String usertoken;
	@Value("${customerCreationurl}")
	private String customerCreationurl;

	@Value("${getEmiBreakUp}")
	private String getEmiBreakUp;

	@Value("${loanCreationUrl}")
	private String loanCreationUrl;

	@Value("${loanCreationTokenUrl}")
	private String loanCreationTokenUrl;

	@Value("${xapikey}")
	private String xapikey;
	@Value("${generationTokenUrl}")
	private String generationTokenUrl;

	@Value("${docUploadUrl}")
	private String docUploadUrl;

	@Autowired
	BorrowerBankRepository borrowerBankRepository;
	@Autowired
	BorrowerRepository borrowerRepository;
	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Autowired
	BorrowerCibilRepository borrowerCibilRepository;

	@Autowired
	BorrowerDocRepository borrowerDocRepository;

	@Autowired
	EmiBreakUpRepository emiBreakUpRepository;

	@Autowired
	ProductService productService;

	@Autowired
	ApiService apiService;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	RuleEngineService engineService;

	@Override
	public Map<String, String> callAllCloudApi(Borrower borrowerObj) {
		Map<String, String> responseMap = new HashMap<>();

		BorrowerCibil borrowerCibil = fetchAndSaveCibilData(borrowerObj);
		RuleEngine ruleEngine = engineService.findRuleEngine(borrowerObj.getLenderUid(), borrowerObj.getProductId());

		if (checkAgeRules(ruleEngine, borrowerObj) || checkCibilScore(ruleEngine, borrowerObj, borrowerCibil)) {
			saveBorrowerAndSendWebhook(borrowerObj);
			return generateFailureResponse(responseMap, "Loan Failed. Validation failed.");
		}

		if (borrowerObj.getCustomerId() == null && !createCustomer(borrowerObj)) {
			saveBorrowerAndSendWebhook(borrowerObj);
			return generateFailureResponse(responseMap, "Loan Failed. Customer creation failed.");
		}

		if (!createLoan(borrowerObj, responseMap)) {
			saveBorrowerAndSendWebhook(borrowerObj);
			return responseMap;
		}

		fetchLoanDetails(borrowerObj);
		saveBorrowerAndSendWebhook(borrowerObj);

		return responseMap;
	}

	// ------ Below are Helper methods to modularize --------

	private BorrowerCibil fetchAndSaveCibilData(Borrower borrowerObj) {
		// Fetch latest record for this borrower
		BorrowerCibil latestCibil = borrowerCibilRepository
				.findTopByBorrowerUidOrderByCreatedOnDesc(borrowerObj.getCustomerLosId());

		// If latest record exists and is less than 90 days old → reuse
		if (latestCibil != null && latestCibil.getCreatedOn() != null
				&& latestCibil.getCreatedOn().isAfter(LocalDateTime.now().minusDays(90))) {
			log.info("CIBIL data already exists for borrower {} and createdOn {}", latestCibil.getBorrowerUid(),
					latestCibil.getCreatedOn());
			return latestCibil;
		}

		// Otherwise → fetch new report
		String creditScoreResponse;
		try {
			creditScoreResponse = apiService.getCreditScore(borrowerObj);
		} catch (Exception e) {
			log.error("Error fetching CIBIL data for borrower {}: {}", borrowerObj.getCustomerId(), e.getMessage(), e);
			throw new RuntimeException("Unable to fetch CIBIL data", e);
		}

		JSONObject data = ValidatedRequest.validateCreditScore(creditScoreResponse);
		if (data == null) {
			throw new RuntimeException("Invalid CIBIL response for borrower " + borrowerObj.getCustomerId());
		}

		BorrowerCibil newCibil = new BorrowerCibil();
		newCibil.setBorrowerUid(borrowerObj.getCustomerLosId());
		newCibil.setNtc("008".equalsIgnoreCase(data.optString("code")));

		// Upload raw JSON to S3 and store the reference
		String s3Path = awsS3Service.uploadJsonToS3(
				borrowerObj.getCustomerId() + "_CIBIL_" + System.currentTimeMillis() + ".json", creditScoreResponse);
		newCibil.setCibilResponse(s3Path);

		if (!newCibil.isNtc()) {
			newCibil.setCibilScore(data.optString("SCORE", null));
			newCibil.setActiveCredit(data.optString("CreditAccountActive", null));
			newCibil.setSuitFile(data.optString("CreditAccountDefault", null));
			newCibil.setSettledLoan(data.optString("CreditAccountClosed", null));
			newCibil.setWriteOff(data.optString("CADSuitFiledCurrentBalance", null));
			newCibil.setFirstYearDPD(data.optString("FIRST_YEAR", null));
			newCibil.setSecondYearDPD(data.optString("SECOND_YEAR", null));
			newCibil.setCaisAccountDetails(data.optString("ACCOUNT_DETAILS", null));
			newCibil.setDpd(data.optString("DAYS_PAST_DUE", null));
		}

		newCibil.setCreatedOn(LocalDateTime.now());
		borrowerCibilRepository.save(newCibil);

		log.info("New CIBIL data saved successfully for borrower {}", borrowerObj.getCustomerId());
		return newCibil;
	}

	private boolean checkAgeRules(RuleEngine ruleEngine, Borrower borrowerObj) {
		LocalDate dob = borrowerObj.getDateOfBirth();
		if (dob == null) {
			log.warn("Date of Birth is null for borrower");
			return false;
		}
		int borrowerAge = Period.between(dob, LocalDate.now()).getYears();

		if ("Active".equalsIgnoreCase(ruleEngine.getMaxAgeStatus())
				&& borrowerAge > Integer.parseInt(ruleEngine.getMaxAgeValue())) {
			log.info("Borrower's age {} exceeds maximum allowed age {}", borrowerAge, ruleEngine.getMaxAgeValue());
			failBorrower(borrowerObj, "Loan Failed. Borrower's age exceeds maximum allowed age");
			return true;
		}

		if ("Active".equalsIgnoreCase(ruleEngine.getMinAgeStatus())
				&& borrowerAge < Integer.parseInt(ruleEngine.getMinAgeValue())) {
			log.info("Borrower's age {} is below minimum required age {}", borrowerAge, ruleEngine.getMinAgeValue());
			failBorrower(borrowerObj, "Loan Failed. Borrower's age is below minimum required age");
			return true;
		}
		return false;
	}

	private boolean checkCibilScore(RuleEngine ruleEngine, Borrower borrowerObj, BorrowerCibil borrowerCibil) {
		if (borrowerCibil.isNtc()) {
			log.info("User is NTC, skipping CIBIL validation");
			return false;
		}
		if ("Active".equalsIgnoreCase(ruleEngine.getCibilStatus())) {
			int minCibil = Integer.parseInt(ruleEngine.getCibilValue());
			int cibilScore = Integer.parseInt(borrowerCibil.getCibilScore());

			if (cibilScore < minCibil) {
				log.info("CIBIL score {} is below minimum required CIBIL score {}", cibilScore, minCibil);
				failBorrower(borrowerObj, "Loan Failed. CIBIL score is below minimum required");
				return true;
			}
		}
		return false;
	}

	private boolean createCustomer(Borrower borrowerObj) {
		JSONObject createCustomer = createJsonForCustomer(borrowerObj);
		String response = callApiForCustomerCreation(borrowerObj, createCustomer);
		String customerCreationResponse = callApiForCustomerCreationWithHmac(response, createCustomer);

		try {
			JSONObject jsonObject = new JSONObject(customerCreationResponse);
			if (jsonObject.has("CustomerId")) {
				borrowerObj.setCustomerId(jsonObject.optString("CustomerId"));
				return true;
			}
		} catch (Exception e) {
			String customerId = extractCIFId(customerCreationResponse);
			if (customerId != null) {
				borrowerObj.setCustomerId(customerId);
				return true;
			}
		}
		failBorrower(borrowerObj, "Loan Failed during Customer creation.");
		return false;
	}

	private boolean createLoan(Borrower borrowerObj, Map<String, String> responseMap) {
		JSONObject createloan = createJsonForLoan(borrowerObj);
		String response = callApiForLoanCreation(borrowerObj, createloan);
		String financeResponse = callApiForLoanCreationWithHmac(response, createloan);

		try {
			JSONObject jsonObject = new JSONObject(financeResponse);
			JSONObject leadFinanceDTO = jsonObject.optJSONObject("LeadFinanceDTO");
			if (leadFinanceDTO != null && leadFinanceDTO.has("FinanceId")) {
				borrowerObj.setResponseCode(ResponseCodeEnum.SUCCESS.getCode());
				borrowerObj.setFinanceId(leadFinanceDTO.optInt("FinanceId"));
				borrowerObj.setLoanStatus(LoanStatus.LOAN_SUCCESSFUL);
				responseMap.put("code", "000");
				responseMap.put("message", "Loan created successfully.");
				return true;
			}
		} catch (Exception e) {
			log.error("Loan Creation Exception : {}", e.getMessage());
		}
		failBorrower(borrowerObj, financeResponse);
		responseMap.put("code", "80989");
		responseMap.put("message", "Loan Failed. Please contact us.");
		return false;
	}

	private void fetchLoanDetails(Borrower borrowerObj) {
		String getApiHmac = callHmacGetApi(borrowerObj);
		String emiBreakUp = callApiForGetLoanAgreementNoAsync(getApiHmac, borrowerObj);
		JSONObject response = new JSONObject(emiBreakUp);

		borrowerObj.setFinanceId(response.optInt("FinanceId"));
		borrowerObj.setEmiAmount(response.optDouble("EMI"));
		borrowerObj.setTotalLoanAmount(response.optDouble("LoanTotalDue"));

		double loanAmount = response.optDouble("TotalAmount");
		double totalLoanAmount = response.optDouble("LoanTotalDue");
		double totalInterest = totalLoanAmount - loanAmount;

		borrowerObj.setSectionAmount(loanAmount);
		borrowerObj.setApr(response.optDouble("EffectiveAPRPercente"));
		borrowerObj.setTotalInterest(totalInterest);

		LocalDate startDate = LocalDate.parse(response.optString("StartDate").substring(0, 10));
		LocalDate endDate = LocalDate.parse(response.optString("EMIEndDate").substring(0, 10));
		borrowerObj.setLoanDays(ChronoUnit.DAYS.between(startDate, endDate));

		JSONArray repaymentSchedules = response.optJSONArray("RepaymentSchedules");
		for (int i = 0; i < repaymentSchedules.length(); i++) {
			JSONObject schedule = repaymentSchedules.getJSONObject(i);
			EmiBreakUp breakUp = new EmiBreakUp();
			breakUp.setBorrowerUid(borrowerObj.getBorrowerUid());
			breakUp.setMobileNo(borrowerObj.getMobileNo());
			breakUp.setLoanAggrement(borrowerObj.getLoanAggrement());
			breakUp.setDueAmount(schedule.optDouble("DueAmount"));
			breakUp.setFinanceId(borrowerObj.getFinanceId());
			breakUp.setDueDate(schedule.optString("strDueDate"));
			breakUp.setInstallmentNo(schedule.optInt("InstallmentNo"));
			breakUp.setPaymentStatus(schedule.optString("PaymentStatus"));
			emiBreakUpRepository.save(breakUp);
		}
	}

	private void failBorrower(Borrower borrowerObj, String message) {
		borrowerObj.setLoanStatus(LoanStatus.LOAN_FAILED);
		borrowerObj.setResponseCode("8777");
		borrowerObj.setResponseMessage(message);
	}

	private Map<String, String> generateFailureResponse(Map<String, String> map, String message) {
		map.put("RESPONSE_CODE", "8777");
		map.put("message", message);
		return map;
	}

	private void saveBorrowerAndSendWebhook(Borrower borrowerObj) {
		borrowerRepository.save(borrowerObj);
		BorrowerCibil borrowerCibils = borrowerCibilRepository
				.findTopByBorrowerUidOrderByCreatedOnDesc(borrowerObj.getCustomerLosId());
		CompletableFuture.runAsync(() -> sendWebhook(borrowerObj, borrowerCibils));
	}

	private void sendWebhook(Borrower borrower, BorrowerCibil borrowerCibils) {

		String webhookUrl = borrower.getWebhookUrl(); // Assuming Borrower has a webhook URL field
		log.info("Webhook URL: {} , loanAggrement: {}", webhookUrl,borrower.getLoanAggrement());
		if (webhookUrl == null || !webhookUrl.startsWith("https://")) {
			log.error("Webhook URL is not valid: {}", webhookUrl);
			return;
		}

		int maxRetries = 5;
		int attempt = 0;
		boolean success = false;

		while (attempt < maxRetries && !success) {
			HttpURLConnection httpConn = null;
			try {

				JSONObject json = new JSONObject();
				json.put("mobileNo", borrower.getMobileNo());
				json.put("loanAggrement", borrower.getLoanAggrement());
				json.put("borrowerUid", borrower.getBorrowerUid());
				json.put("uid", borrower.getCustomerLosId());
				json.put("emi", borrower.getEmiAmount());
				json.put("totalLoanAmount", borrower.getTotalLoanAmount());
				json.put("firstEmiDate", borrower.getEmiDate());
				json.put("loanStatus", borrower.getLoanStatus());
				json.put("ntc", borrowerCibils.isNtc());
				if (!borrowerCibils.isNtc()) {
					json.put("cibilScore", borrowerCibils.getCibilScore());
				} else {
					json.put("cibilScore", "-1");
				}
				// Convert borrower object to JSON
				String jsonPayload = json.toString();
				log.info("Webhook Payload: {}", jsonPayload);
				// Create URL object
				URL url = new URL(webhookUrl);
				httpConn = (HttpURLConnection) url.openConnection();

				// Set HTTP method and headers
				httpConn.setRequestMethod("POST");
				httpConn.setRequestProperty("Content-Type", "application/json");
				httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
				httpConn.setDoOutput(true);

				// Send JSON payload
				try (OutputStream os = httpConn.getOutputStream()) {
					byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
					os.write(input, 0, input.length);
				}

				// Get response code
				int responseCode = httpConn.getResponseCode();
				log.info("Webhook Attempt " + (attempt + 1) + " Response Code: " + responseCode);

				// Read response
				BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				log.info("Webhook Response: " + response.toString());

				// Check if response is 2xx (success)
				if (responseCode >= 200 && responseCode < 300) {
					success = true;
				} else {
					log.error("Non-2xx response: " + responseCode);
				}

			} catch (Exception e) {
				log.error("Webhook attempt " + (attempt + 1) + " failed: " + e.getMessage());

				// If this was the last attempt, log failure
				if (attempt == maxRetries - 1) {
					log.error("Webhook failed after " + maxRetries + " attempts.");
				} else {
					// Wait 10 minutes before retrying
					try {
						log.info("Retrying in 10 minutes...");
						Thread.sleep(10 * 60 * 1000); // 10 minutes
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						return;
					}
				}
			} finally {
				if (httpConn != null) {
					httpConn.disconnect();
				}
			}
			attempt++;
		}

	}

	private String callApiForGetLoanAgreementNoAsync(String getApiHmac, Borrower borrower) {
		try {
			String generationUrl = getEmiBreakUp.concat(borrower.getLoanAggrement());
//			URL url = new URL(generationUrl);
//			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//			httpConn.setRequestMethod("GET");

			URL url = new URL(generationUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Authorization", getApiHmac.replaceAll("\"", ""));

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud Fatch EMI Api Response= {}", response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud Fatch EMI Api Error", e);
		}
		return "";

	}

	private String callHmacGetApi(Borrower borrower) {

		try {

			String generationUrl = getEmiBreakUp.concat(borrower.getLoanAggrement());
			URL url = new URL(generationTokenUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("appid", appid);
			httpConn.setRequestProperty("secrettoken", secrettoken);
			httpConn.setRequestProperty("usertoken", usertoken);
			httpConn.setRequestProperty("url", generationUrl);
			httpConn.setRequestProperty("x-api-key", xapikey);
			httpConn.setRequestProperty("Content-Type", "application/json");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
//			writer.write(createCustomer.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud EMI callHmacGetApi HMAC Creation Api Error", e);
		}
		return "";

	}

	private String callApiForLoanCreationWithHmac(String respom, JSONObject createloan) {
		try {
			URL url = new URL(loanCreationUrl);

			// Prepare curl command for debugging
			String curlCommand = String.format(
					"curl -X POST '%s' \\\n" + "  -H 'Content-Type: application/json' \\\n"
							+ "  -H 'Authorization: %s' \\\n" + "  -d '%s'",
					loanCreationUrl, respom.replaceAll("\"", ""), createloan.toString().replace("'", "\\'"));

			log.info("Equivalent CURL Command:\n{}", curlCommand);

			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", respom.replaceAll("\"", ""));

			httpConn.setDoOutput(true);
			try (OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream())) {
				writer.write(createloan.toString().trim());
			}

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud Loan Creation Api Response= {}", response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud Loan Creation Api Error", e);
		}
		return "";
	}

	private String callApiForLoanCreation(Borrower borrower, JSONObject createloan) {
		try {
			URL url = new URL(generationTokenUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("appid", appid);
			httpConn.setRequestProperty("secrettoken", secrettoken);
			httpConn.setRequestProperty("usertoken", usertoken);
			httpConn.setRequestProperty("url", loanCreationTokenUrl);
			httpConn.setRequestProperty("x-api-key", xapikey);
			httpConn.setRequestProperty("Content-Type", "application/json");

			// Print equivalent CURL command for debugging
			String curlCommand = String.format(
					"curl -X POST '%s' \\\n" + "  -H 'appid: %s' \\\n" + "  -H 'secrettoken: %s' \\\n"
							+ "  -H 'usertoken: %s' \\\n" + "  -H 'url: %s' \\\n" + "  -H 'x-api-key: %s' \\\n"
							+ "  -H 'Content-Type: application/json' \\\n" + "  -d '%s'",
					generationTokenUrl, appid, secrettoken, usertoken, loanCreationTokenUrl, xapikey,
					createloan.toString().replace("'", "\\'"));

			log.info("Equivalent CURL Command:\n{}", curlCommand);

			httpConn.setDoOutput(true);
			try (OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream())) {
				writer.write(createloan.toString());
			}

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("Response of ApiForLoanCreation: {} ", response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud Loan HMAC Creation Api Error", e);
		}
		return "";
	}

	private JSONObject createJsonForLoan(Borrower borrower) {

		try {

			Product product = productService.getProductByProductIdAndUid(borrower.getProductId(),
					borrower.getLenderUid());
			JSONObject leadDetails = new JSONObject();

			leadDetails.put("CompanyRoleId", 198);
			leadDetails.put("DealerId", JSONObject.NULL);
			leadDetails.put("LeadDetailId", 0);

			// LeadFinanceDTO

			LocalDate currentDate = LocalDate.now();

			// Format the updated date using DateTimeFormatter
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String todayDate = currentDate.format(dateFormatter);

			JSONObject leadFinanceDTO = new JSONObject();
			leadFinanceDTO.put("AgreementNo", borrower.getLoanAggrement());
			leadFinanceDTO.put("Duration", borrower.getEmiTime());
			leadFinanceDTO.put("EMI", borrower.getEmiAmount());

			leadFinanceDTO.put("EMIStartDate", borrower.getEmiDate());// Next emi day + 1
																		// month
			leadFinanceDTO.put("EMITypeId", 1);
			leadFinanceDTO.put("FinanceId", 0);
			leadFinanceDTO.put("IndicativeROI", borrower.getEmiRate());
			leadFinanceDTO.put("InstallmentTypeId", 0);
			leadFinanceDTO.put("LPCAmount", 0);
			leadFinanceDTO.put("LPCInterest", 0);
			leadFinanceDTO.put("LPCType", 0);
			leadFinanceDTO.put("EMI", JSONObject.NULL);

			// PLAssetDetailDTO
			JSONObject plAssetDetailDTO = new JSONObject();
			plAssetDetailDTO.put("EmploymentType", 0);
			plAssetDetailDTO.put("InsuranceAmount", 0);
			plAssetDetailDTO.put("InsuranceExpireDate", todayDate + "T13:55:40.347Z");
			plAssetDetailDTO.put("InsuranceNo", "4545");

			leadFinanceDTO.put("PLAssetDetailDTO", plAssetDetailDTO);

			leadFinanceDTO.put("ROIDuration", 0);
			leadFinanceDTO.put("SanctionAmount", borrower.getLoanAmount());
			leadFinanceDTO.put("StartDate", todayDate);
			leadFinanceDTO.put("TotalAmount", borrower.getLoanAmount());

			if (borrower.getLoanType().equals("Early_Salary")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate date1 = LocalDate.parse(borrower.getEmiDate(), formatter);
				LocalDate date2 = LocalDate.parse(todayDate, formatter);

				long daysBetween = ChronoUnit.DAYS.between(date2, date1);
				Double amDouble = borrower.getLoanAmount().doubleValue();

				Double emiRate = Double.valueOf(borrower.getEmiRate());

				Double interest = (emiRate / 100) * amDouble;

				Double dailyInterest = interest / 365;

				Double totalInterest = dailyInterest * daysBetween;

				int interestDays = totalInterest.intValue();

				leadFinanceDTO.put("TotalAmount", new BigDecimal(borrower.getLoanAmount()));

				JSONArray vasArray = new JSONArray();

				JSONObject vas = new JSONObject();
				vas.put("Amount", interestDays);
				vas.put("DueDate", borrower.getEmiDate());
				vas.put("IncomeRecognition", "DeductNow");
				vas.put("Name", "Interest Fees");
				vas.put("VASPercentage", 0);
				vas.put("VASTypeId", 38);
				vas.put("Name", "Broken Interest");

				JSONObject vas2 = new JSONObject();
				vas2.put("Amount", product.getProcessingFee());
				vas2.put("DueDate", borrower.getEmiDate());
				vas2.put("IncomeRecognition", "DeductNow");
				vas2.put("Name", "Processing Fees");
				vas2.put("VASPercentage", 0);
				vas2.put("VASTypeId", "10");

				vasArray.put(vas);
				vasArray.put(vas2);

				// Add the array to leadFinanceDTO
				leadFinanceDTO.put("VASs", vasArray);
//				leadFinanceDTO.append("VASs", vas2);

			}

			leadFinanceDTO.put("YearlyInterest", borrower.getEmiRate());

			leadDetails.put("LeadFinanceDTO", leadFinanceDTO);

			leadDetails.put("LeadSourceDetailId", 5);// lead source id value as it is -
			// borrower.getLeadSourceId()
			leadDetails.put("LeadSourceId", "Dealer");
			if (product.getPartnershipType().equalsIgnoreCase("Co-Lending")) {
				leadDetails.put("LeadSourceId", "BSP");
				leadDetails.put("OpLenderSchemeId", product.getContractedIrr());
			}
			leadDetails.put("LoanCategoryId", "General");
			leadDetails.put("LoanSegmentId", 1);
			leadDetails.put("LoanStateForPL", 6);

			// lstLeadCustomers
			JSONObject leadCustomers = new JSONObject();
			leadCustomers.put("BorrowerId", Integer.parseInt(borrower.getCustomerId()));
			leadCustomers.put("BorrowerTypeId", 0);
			leadCustomers.put("OrderTypeId", 0);
			leadCustomers.put("RelationToBorrower", 53);

			leadDetails.append("lstLeadCustomers", leadCustomers);

			leadDetails.put("PaymentTaskDTO", JSONObject.NULL);

			// PLSalaryBorrower

			String houseAddress = "";

			BorrowerAadhaar borrowerAadhar = borrowerAadhaarRepository.findByBorrowerUid(borrower.getCustomerLosId());
			if (borrowerAadhar.getHouseInAadhar() != null && !borrowerAadhar.getHouseInAadhar().isEmpty()) {
				houseAddress = borrowerAadhar.getHouseInAadhar();
			} else if (borrowerAadhar.getLocInAadhar() != null && !borrowerAadhar.getLocInAadhar().isEmpty()) {
				houseAddress = borrowerAadhar.getLocInAadhar();
			} else if (borrowerAadhar.getPoInAadhar() != null && !borrowerAadhar.getPoInAadhar().isEmpty()) {
				houseAddress = borrowerAadhar.getPoInAadhar();
			} else {
				houseAddress = "DELHI";
			}
			String PrimaryArea = "";
			if (borrowerAadhar.getStreetInAadhar() != null && !borrowerAadhar.getStreetInAadhar().isEmpty()) {
				PrimaryArea = borrowerAadhar.getStreetInAadhar();
			} else if (borrowerAadhar.getSubdistInAadhar() != null && !borrowerAadhar.getSubdistInAadhar().isEmpty()) {
				PrimaryArea = borrowerAadhar.getSubdistInAadhar();
			} else if (borrowerAadhar.getDistInAadhar() != null && !borrowerAadhar.getDistInAadhar().isEmpty()) {
				PrimaryArea = borrowerAadhar.getDistInAadhar();
			} else {
				PrimaryArea = "DELHI";
			}

			String PrimaryTown = "";
			if (borrowerAadhar.getDistInAadhar() != null && !borrowerAadhar.getDistInAadhar().isEmpty()) {
				PrimaryTown = borrowerAadhar.getDistInAadhar();
			} else {
				PrimaryTown = "DELHI";
			}
			double totalSalary = 200000;
			if (borrower.getTotalSalary() != 0) {
				totalSalary = borrower.getTotalSalary();
			}
			JSONObject plSalaryBorrower = new JSONObject();
			plSalaryBorrower.put("AddressLine1", houseAddress);
			plSalaryBorrower.put("AnnualSalary", totalSalary);
			plSalaryBorrower.put("Area", PrimaryArea);
			plSalaryBorrower.put("Department", "dept");
			plSalaryBorrower.put("Designation", borrower.getDesignation());
			plSalaryBorrower.put("EmployeeId", "vtpl323");
			plSalaryBorrower.put("EmployerName", borrower.getEmployerName());
			plSalaryBorrower.put("JoiningDate", "2018-08-19T13:55:40.347Z");
			plSalaryBorrower.put("Landmark", PrimaryArea);
			plSalaryBorrower.put("NoOfEmployees", 10);
			plSalaryBorrower.put("OrganizationType", 0);
			plSalaryBorrower.put("Postcode", borrowerAadhar.getZipInAadhar());
			plSalaryBorrower.put("Sector", PrimaryTown);
			plSalaryBorrower.put("Segment", "General");
			plSalaryBorrower.put("StateId", 1);
			plSalaryBorrower.put("SubSegment", "subseg1");
			plSalaryBorrower.put("Town", PrimaryTown);

			leadDetails.put("PLSalaryBorrower", plSalaryBorrower);

			leadDetails.put("ProductTypeId", borrower.getProductTypeId());
			leadDetails.put("SchemeId", Integer.valueOf(borrower.getSchemeId()));

			log.info("CreateLoan API request: " + leadDetails.toString());

			return leadDetails;

		} catch (Exception e) {
			log.error("Error in CreateLoan API", e);

			return null;
		}
	}

	public String callApiForDocUploadWithHmac(String respom, JSONObject createCustomer) {
		try {
			log.info("All Cloud DocUpload Api Url= {}", docUploadUrl);
			log.info("All Cloud DocUpload Api Request= {}", createCustomer.toString().trim());
			log.info("All Cloud DocUpload Api Hmac= {}", respom);
			URL url = new URL(docUploadUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", respom.replaceAll("\"", ""));

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(createCustomer.toString().trim());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud DocUpload  Api Response= {}", response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud DocUpload Api Error", e);
		}
		return "";

	}

	private String callApiForDocUploadCreation(Borrower borrower, JSONObject documentUpload) {
		try {

			URL url = new URL(generationTokenUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("appid", appid);
			httpConn.setRequestProperty("secrettoken", secrettoken);
			httpConn.setRequestProperty("usertoken", usertoken);
			httpConn.setRequestProperty("url", docUploadUrl);
			httpConn.setRequestProperty("x-api-key", xapikey);
			httpConn.setRequestProperty("Content-Type", "application/json");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(documentUpload.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info(response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud DocUpload HMAC Creation Api Error", e);
		}
		return "";

	}

	private JSONObject createJsonForDocx(Borrower borrower, String base64, String docName) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("EntityId", Integer.valueOf(borrower.getCustomerId()));
		jsonObject.put("EntityTypeId", "Customer");
		JSONObject UploadDocumentDTO = new JSONObject();
		UploadDocumentDTO.put("DocType", docName);
		UploadDocumentDTO.put("Order", 1);
		UploadDocumentDTO.put("FileName", "docUser.jpg");
		UploadDocumentDTO.put("UploadDocBase64", base64);
		jsonObject.put("UploadDocumentDTO", UploadDocumentDTO);
		return jsonObject;
	}

	private String callAwsS3ApiForDocUpload(String userImageUrl) {
		try {
			Mono<String> response = webClient.get().uri(userImageUrl).retrieve().bodyToMono(String.class);
			return response.block(); // Note: Avoid .block() in a reactive context if possible.
		} catch (Exception e) {
			log.error("Error in callAwsS3ApiForDocUpload", e);
			return null;
		}
	}

	private String extractCIFId(String customerCreationResponse) {
		String targetString = "CIF Id - ";
		int index = customerCreationResponse.indexOf(targetString);

		if (index != -1) {
			int cifStartIndex = index + targetString.length();
			int cifEndIndex = customerCreationResponse.indexOf(",", cifStartIndex);

			if (cifEndIndex == -1) {
				cifEndIndex = customerCreationResponse.length();
			}

			return customerCreationResponse.substring(cifStartIndex, cifEndIndex).trim();
		}

		return null;
	}

	private String callApiForCustomerCreationWithHmac(String respom, JSONObject createCustomer) {
		try {

			log.info("callApiForCustomerCreationWithHmac{}", createCustomer.toString());
			URL url = new URL(customerCreationurl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", respom.replaceAll("\"", ""));

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(createCustomer.toString().trim());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud Customer Creation Api Response= {}", response);
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud Customer Creation Api Error", e);
		}
		return "";

	}

	private String callApiForCustomerCreation(Borrower borrower, JSONObject createCustomer) {
		try {

			log.info("callApiForCustomerCreation  HMAC{}", createCustomer.toString());
			URL url = new URL(generationTokenUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("appid", appid);
			httpConn.setRequestProperty("secrettoken", secrettoken);
			httpConn.setRequestProperty("usertoken", usertoken);
			httpConn.setRequestProperty("url", customerCreationurl);
			httpConn.setRequestProperty("x-api-key", xapikey);
			httpConn.setRequestProperty("Content-Type", "application/json");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(createCustomer.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				return response;
			}
		} catch (Exception e) {
			log.error("All Cloud Customer HMAC Creation Api Error", e);
		}
		return "";

	}

	private JSONObject createJsonForCustomer(Borrower borrower) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("CustomerId", 0);
		String[] names1 = extractNames(borrower.getFullName());
		String firstName = "";
		if (names1[0] != null && !names1[0].isEmpty()) {
			firstName = names1[0];
		}
		jsonObject.put("FirstName", firstName);
		String lastName = "";
		if (names1[2] != null && !names1[2].isEmpty()) {
			lastName = names1[2];
		}

		String middleName = "";
		if (names1[1] != null && !names1[1].isEmpty()) {
			middleName = names1[1];
		}
		jsonObject.put("LastName", lastName);
		jsonObject.put("DOB", String.valueOf(borrower.getDateOfBirth()).concat("T12:06:24.071Z"));
		jsonObject.put("ContactNumber", borrower.getMobileNo().replaceAll("\"", ""));
		jsonObject.put("LandLineNumber", "");
		jsonObject.put("Passport", "");
		Customer borrowerPan = customerRepository.findByUid(borrower.getCustomerLosId());

		jsonObject.put("PANCard", borrowerPan.getPanNumber());
		jsonObject.put("RationCard", "");
		jsonObject.put("DrivingLicense", "");
		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(borrower.getCustomerLosId());
		jsonObject.put("AadarCard", borrowerAadhaar.getAadharNumber());
		jsonObject.put("lstKYCTypeDetails", "");
		jsonObject.put("AdditionalInfo", "");
		jsonObject.put("FatherName", "");
		if (borrowerAadhaar.getCareOfInAadhar() != null && !borrowerAadhaar.getCareOfInAadhar().isEmpty()) {
			jsonObject.put("FatherName", borrowerAadhaar.getCareOfInAadhar());
		}

		jsonObject.put("ResidentTypeId", "0");
		jsonObject.put("ResidentialStatusId", "ResidentIndian");
		jsonObject.put("OwnerName", "");
		jsonObject.put("Occupation", "");
		jsonObject.put("StayDuration", 0);
		jsonObject.put("VoterId", "");
		if (borrower.getEmailId() != null && !borrower.getEmailId().isEmpty()) {
			jsonObject.put("Email", borrower.getEmailId());
		}

		jsonObject.put("EntityType", "Individual");
		jsonObject.put("CustomerTypeDetailId", 13);
		jsonObject.put("CustomerCategoryTypeId", "B");
		jsonObject.put("Salutation", "");
		jsonObject.put("MiddleName", middleName);

		jsonObject.put("Gender", "");// need to check Male,Female

		if (borrower.getGender() != null && !borrower.getGender().isEmpty()) {
			jsonObject.put("Gender", borrower.getGender());
		}
		jsonObject.put("MaritalStatus", "");

		if (borrower.getMaritalStatus() != null && !borrower.getMaritalStatus().isEmpty()) {
			jsonObject.put("MaritalStatus", borrower.getMaritalStatus());
		}
		jsonObject.put("CitizenshipTypeId", "Indian");
		jsonObject.put("Caste", "Other");
		jsonObject.put("Religion", "");
		JSONObject proofOfAddressJsonDetails = new JSONObject();
		JSONObject addressType = new JSONObject();
		addressType.put("Residence", true);
		addressType.put("Business", false);
		addressType.put("RegisteredOffice", false);
		addressType.put("Unspecified", true);
		JSONObject poa = new JSONObject();
		poa.put("Passport", false);
		poa.put("DrivingLicense", false);
		poa.put("UID", true);
		poa.put("UtilityBill", false);
		poa.put("Others", false);
		poa.put("OtherProof", true);
		proofOfAddressJsonDetails.put("AddressType", addressType);
		proofOfAddressJsonDetails.put("PoA", poa);
		jsonObject.put("ProofOfAddressJsonDetails", proofOfAddressJsonDetails);
		jsonObject.put("EmploymentType", "");
		jsonObject.put("AnnualIncome", 0);
		jsonObject.put("AlternateMobileNo", borrower.getMobileNo().replaceAll("\"", ""));

		BorrowerBank borrowerAccount = borrowerBankRepository.findByBorrowerUid(borrower.getCustomerLosId());
		jsonObject.put("BankAccountType", "savings");
		jsonObject.put("ElectricityAccountNo", "");
		jsonObject.put("GASAccountNo", "");
		jsonObject.put("DTHAccountNo", "");
		jsonObject.put("UDF1", "");
		jsonObject.put("UDF2", "");
		jsonObject.put("UDF3", "");
		jsonObject.put("UDF4", "");
		jsonObject.put("UDF5", "");
		jsonObject.put("UDF6", "");
		jsonObject.put("UDF7", "");
		jsonObject.put("UDF8", "");
		jsonObject.put("UDF9", "");
		jsonObject.put("UDF10", "");
		jsonObject.put("BankNo", borrowerAccount.getBankAccountNo());
		jsonObject.put("CentreName", "");
		jsonObject.put("MaskedPANCard", "");
		jsonObject.put("MaskedPassport", "");
		jsonObject.put("MaskedVoterId", "");
		jsonObject.put("MaskedDrivingLicense", "");
		jsonObject.put("MaskedRationCard", "");
		jsonObject.put("MaskedAadarCard", "");
		jsonObject.put("BankDetailId", 0);
		jsonObject.put("IFSCCode", borrowerAccount.getIfscCode());
		jsonObject.put("BranchName", "");// banker.getBranchName().trim());
		if (borrowerAccount.getBankBranchName() != null && !borrowerAccount.getBankBranchName().isEmpty()) {
			jsonObject.put("BranchName", borrowerAccount.getBankBranchName());
		}
		jsonObject.put("BankName", borrowerAccount.getIfscCode().substring(0, 4));
		jsonObject.put("BankAccountName", borrower.getFullName().trim());

		String houseAddress = "";
		if (borrowerAadhaar.getHouseInAadhar() != null && !borrowerAadhaar.getHouseInAadhar().isEmpty()) {
			houseAddress = borrowerAadhaar.getHouseInAadhar();
		} else if (borrowerAadhaar.getLocInAadhar() != null && !borrowerAadhaar.getLocInAadhar().isEmpty()) {
			houseAddress = borrowerAadhaar.getLocInAadhar();
		} else if (borrowerAadhaar.getPoInAadhar() != null && !borrowerAadhaar.getPoInAadhar().isEmpty()) {
			houseAddress = borrowerAadhaar.getPoInAadhar();
		} else {
			houseAddress = "DELHI";
		}
		jsonObject.put("PrimaryAddressLine1", houseAddress);
		String PrimaryArea = "";
		if (borrowerAadhaar.getStreetInAadhar() != null && !borrowerAadhaar.getStreetInAadhar().isEmpty()) {
			PrimaryArea = borrowerAadhaar.getStreetInAadhar();
		} else if (borrowerAadhaar.getSubdistInAadhar() != null && !borrowerAadhaar.getSubdistInAadhar().isEmpty()) {
			PrimaryArea = borrowerAadhaar.getSubdistInAadhar();
		} else if (borrowerAadhaar.getDistInAadhar() != null && !borrowerAadhaar.getDistInAadhar().isEmpty()) {
			PrimaryArea = borrowerAadhaar.getDistInAadhar();
		} else {
			PrimaryArea = "DELHI";
		}

		jsonObject.put("PrimaryArea", PrimaryArea);

		String PrimaryTown = "";
		if (borrowerAadhaar.getDistInAadhar() != null && !borrowerAadhaar.getDistInAadhar().isEmpty()) {
			PrimaryTown = borrowerAadhaar.getDistInAadhar();
		} else {
			PrimaryTown = "DELHI";
		}
		jsonObject.put("PrimaryTown", PrimaryTown);
		jsonObject.put("PrimaryPostcode", borrowerAadhaar.getZipInAadhar());
		jsonObject.put("PrimaryStateId", 25);
		jsonObject.put("PrimaryStateName", borrower.getSecondaryState().replaceAll(" ", ""));
		jsonObject.put("PrimaryLandmark", "");
		jsonObject.put("PrimaryPoliceStation", "");
		jsonObject.put("AlternateEmail", borrower.getEmailId());
		jsonObject.put("FamilyBackground", "");
		jsonObject.put("lstCreditBureauAudit", "");
		jsonObject.put("LstTaggingDTO", "");
		jsonObject.put("UploadDocumentDTOCollection", "");
		jsonObject.put("SecondaryAddressLine1", borrower.getSecondaryAddressLine1());
		jsonObject.put("SecondaryArea", borrower.getSecondaryArea());
		jsonObject.put("SecondaryTown", borrower.getSecondaryCity());
		jsonObject.put("SecondaryPostcode", borrower.getSecondaryPinCode());
		jsonObject.put("SecondaryStateId", 25);
		jsonObject.put("SecondaryStateName", borrower.getSecondaryState().replaceAll(" ", ""));
		jsonObject.put("SecondaryLandmark", borrower.getSecondaryLandmark());
		jsonObject.put("SecondaryPoliceStation", borrower.getSecondaryCity());
		jsonObject.put("IsLead", true);

		return jsonObject;
	}

	private String[] extractNames(String fullName) {
		String[] names = fullName.trim().split("\\s+");

		if (names.length == 1) { // Single name
			return new String[] { names[0], "", "" };
		} else if (names.length == 2) { // Two-part name
			return new String[] { names[0], "", names[1] };
		} else { // Multiple names
			String firstName = names[0];
			String middleName = "";
			String lastName = names[names.length - 1];

			// Concatenate middle names if multiple exist
			for (int i = 1; i < names.length - 1; i++) {
				middleName += names[i] + " ";
			}
			middleName = middleName.trim();

			return new String[] { firstName, middleName, lastName };
		}
	}

}
