package com.mixo.serviceimpl;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mixo.dto.AadharVerificationRequestDto;
import com.mixo.dto.PanVerificationRequest;
import com.mixo.dto.VerificationResponse;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerPan;
import com.mixo.model.Customer;
import com.mixo.model.UserVerificationStatus;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.CustomerRepository;
import com.mixo.repository.UserVerificationStatusRepository;
import com.mixo.service.ApiService;
import com.mixo.service.AwsS3Service;
import com.mixo.utils.NameMatch;
import com.mixo.utils.ResponseCodeEnum;
import com.mixo.utils.StateInfo;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApiServiceImpl implements ApiService {

	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	UserVerificationStatusRepository userVerificationStatusRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadharRepository;

	@Autowired
	private WebClient webClient;

	@Autowired
	CustomerRepository customerRepository;

	@Value("${enachReturnurl}")
	private String enachReturnurl;

	@Value("${digio_upload_url}")
	private String digio_upload_url;

	@Value("${digio_token}")
	private String digio_token;

	@Value("${legal_upload_url}")
	private String legal_upload_url;

	@Value("${legal_token}")
	private String legal_token;

	@Value("${passKey}")
	private String passKey;

	@Value("${sign_id}")
	private String sign_id;

	@Value("${signerName}")
	private String signerName;

	@Value("${signerEmail}")
	private String signerEmail;

	@Override
	public String verifyPAN(String panNumber, Optional<Borrower> borrower) {
		String url = "https://apis2.mufingreenfinance.com/pan";

		JSONObject panRequest = new JSONObject();
		panRequest.put("panNo", panNumber);
		panRequest.put("name", borrower.get().getFullName());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = borrower.get().getDateOfBirth().format(formatter);
		panRequest.put("dob", formattedDate);
		panRequest.put("apiSource", "D1");

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class).doOnNext(jsonResponse -> log.info("API Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String getCreditScore(Borrower borrowerObj) {

		try {

			JSONObject cibileRequest = createCibilRequest(borrowerObj);

			URL url = new URL("https://apis2.mufingreenfinance.com/exp/CIR");
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("accessToken", "C8HnJ3wW2RzdANp5");
			httpConn.setRequestProperty("Content-Type", "application/json");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(cibileRequest.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();

			log.info("Cibil Response Code: {}", httpConn.getResponseCode());
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";

				log.info("Cibil Response JSON: {}", response);
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject createCibilRequest(Borrower borrowerObj) {
		JSONObject cibileRequest = new JSONObject();

		BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());
		BorrowerAadhaar borrowerAadhar = borrowerAadharRepository.findByBorrowerUid(borrowerObj.getCustomerLosId());

		String[] names1 = extractNames(borrowerObj.getFullName());
		// Add basic key-value pair
		cibileRequest.put("isUser_Consent", "Y");

		// Create the Applicant JSONObject
		JSONObject applicant = new JSONObject();
		applicant.put("Surname", names1[2]);
		applicant.put("FirstName", names1[0]);
		applicant.put("MiddleName1", names1[1]);
		applicant.put("MiddleName2", "");
		applicant.put("MiddleName3", "");
		if (borrowerObj.getGender().equalsIgnoreCase("Male")) {
			applicant.put("GenderCode", "1");
		} else {
			applicant.put("GenderCode", "2");

		}
		applicant.put("IncomeTaxPAN", borrowerPan.getPanNumber());
		applicant.put("PAN_Issue_Date", "");
		applicant.put("PAN_Expiration_Date", "");
		applicant.put("PassportNumber", "");
		applicant.put("Passport_Issue_Date", "");
		applicant.put("Passport_Expiration_Date", "");
		applicant.put("VoterIdentityCard", "");
		applicant.put("Voter_ID_Issue_Date", "");
		applicant.put("Voter_ID_Expiration_Date", "");
		applicant.put("Driver_License_Number", "");
		applicant.put("Driver_License_Issue_Date", "");
		applicant.put("Driver_License_Expiration_Date", "");
		applicant.put("Ration_Card_Number", "");
		applicant.put("Ration_Card_Issue_Date", "");
		applicant.put("Ration_Card_Expiration_Date", "");
		applicant.put("Universal_ID_Number", "");
		applicant.put("Universal_ID_Issue_Date", "");
		applicant.put("Universal_ID_Expiration_Date", "");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = borrowerObj.getDateOfBirth().format(formatter);

		applicant.put("DateOfBirth", formattedDate);
		applicant.put("STDPhoneNumber", "");
		applicant.put("PhoneNumber", "");
		applicant.put("Telephone_Extension", "");
		applicant.put("Telephone_Type", "");
		applicant.put("MobilePhone", "");
		applicant.put("EMailId", "");

		cibileRequest.put("Applicant", applicant);

		// Create the Details JSONObject
		JSONObject details = new JSONObject();
		details.put("Income", "");
		details.put("MaritalStatus", "");
		details.put("EmployStatus", "");
		details.put("TimeWithEmploy", "");
		details.put("NumberOfMajorCreditCardHeld", "");

		cibileRequest.put("Details", details);

		// Create the Address JSONObject
		JSONObject address = new JSONObject();
		address.put("FlatNoPlotNoHouseNo", "1001");
		address.put("BldgNoSocietyName", "");
		address.put("RoadNoNameAreaLocality", "");
		address.put("City", borrowerAadhar.getDistInAadhar() != null ? borrowerAadhar.getDistInAadhar() : "");
		address.put("Landmark", borrowerAadhar.getPoInAadhar() != null ? borrowerAadhar.getPoInAadhar()
				: borrowerAadhar.getSubdistInAadhar());
		address.put("State",
				StateInfo.getStateCode(borrowerAadhar.getStateInAadhar().toUpperCase().replace(" ", "")) != null
						? StateInfo.getStateCode(borrowerAadhar.getStateInAadhar().toUpperCase().replace(" ", ""))
						: 07);
		address.put("PinCode", borrowerAadhar.getZipInAadhar() != null ? borrowerAadhar.getZipInAadhar() : "");

		cibileRequest.put("Address", address);

		// Create the AdditionalAddressFlag JSONObject
		JSONObject additionalAddressFlag = new JSONObject();
		additionalAddressFlag.put("Flag", "N");

		cibileRequest.put("AdditionalAddressFlag", additionalAddressFlag);

		// Create the AdditionalAddress JSONObject
		JSONObject additionalAddress = new JSONObject();
		additionalAddress.put("FlatNoPlotNoHouseNo", "1001");
		additionalAddress.put("BldgNoSocietyName", "");
		additionalAddress.put("RoadNoNameAreaLocality", "");
		additionalAddress.put("City", borrowerAadhar.getDistInAadhar() != null ? borrowerAadhar.getDistInAadhar() : "");
		additionalAddress.put("Landmark", borrowerAadhar.getPoInAadhar() != null ? borrowerAadhar.getPoInAadhar()
				: borrowerAadhar.getSubdistInAadhar());
		additionalAddress.put("State",
				StateInfo.getStateCode(borrowerAadhar.getStateInAadhar().toUpperCase().replace(" ", "")) != null
						? StateInfo.getStateCode(borrowerAadhar.getStateInAadhar().toUpperCase().replace(" ", ""))
						: 07);
		additionalAddress.put("PinCode",
				borrowerAadhar.getZipInAadhar() != null ? borrowerAadhar.getZipInAadhar() : "");

		cibileRequest.put("AdditionalAddress", additionalAddress);

		log.info("CIBIL REQUEST: " + cibileRequest.toString());
		return cibileRequest;
	}

	public static String[] extractNames(String fullName) {
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

	@Override
	public String aadharOtpApi(String aadharNumber, Optional<Borrower> borrower) {
		String url = "https://apis2.mufingreenfinance.com/sure/aadhaar/sentOtp";

		JSONObject panRequest = new JSONObject();
		panRequest.put("aadhaarNumber", aadharNumber);

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Sent OTP Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String verifyAadhar(String aadharOtp, Optional<Borrower> borrower, String string) {
		String url = "https://apis2.mufingreenfinance.com/sure/aadhaar/verifyOtp";

		JSONObject panRequest = new JSONObject();
		panRequest.put("requestId", string);
		panRequest.put("otp", aadharOtp);

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Verification Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String verifyBankAccount(JSONObject jsonObject) {
		String url = "https://apis2.mufingreenfinance.com/pd/pennydrop";

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(jsonObject.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Banlk Account Verification Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String esign(Borrower borrowerObj, String base64Pdf) {

		String url = digio_upload_url;

		JSONObject signer = new JSONObject();
		signer.put("identifier", borrowerObj.getMobileNo());
		signer.put("name", borrowerObj.getFullName());
		signer.put("sign_type", "aadhaar");
		signer.put("reason", "For KFS E-signing");

		// Creating the signers array
		JSONArray signersArray = new JSONArray();
		signersArray.put(signer);

		// Creating the sign coordinates object
		JSONObject coordinate = new JSONObject();
		coordinate.put("llx", 376.55510204081634);
		coordinate.put("lly", 67.89677419354838);
		coordinate.put("urx", 535.8943577430973);
		coordinate.put("ury", 129.36122241086588);

		JSONObject signCoordinates = new JSONObject();
		JSONObject emailMobile = new JSONObject();
		JSONArray pageArray = new JSONArray();
		pageArray.put(coordinate);
		emailMobile.put("1", pageArray);
		signCoordinates.put("email/mobile", emailMobile);

		// Creating the final JSON object
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("signers", signersArray);
		jsonObject.put("expire_in_days", 2);
		jsonObject.put("display_on_page", "all");
		jsonObject.put("notify_signers", false);
		jsonObject.put("send_sign_link", false);
		jsonObject.put("generate_access_token", true);
		jsonObject.put("file_name", "HINDON_LOAN_AGGREMENT.pdf");
		jsonObject.put("file_data", base64Pdf);
		jsonObject.put("sign_coordinates", signCoordinates);

		Mono<String> response = webClient.post().uri(url).header("Authorization", "Basic " + digio_token)
				.header("Content-Type", "application/json").bodyValue(jsonObject.toString()).retrieve()
				.bodyToMono(String.class).doOnNext(jsonResponse -> log.info("Esign Response JSON: {}", jsonResponse));
		return response.block();
	}

	@Override
	public String esignV2(Borrower borrowerObj, String base64Pdf) {
		String url = legal_upload_url;

		// 1. File Object
		JSONObject fileObj = new JSONObject().put("name", borrowerObj.getLoanAggrement() + ".pdf").put("file",
				base64Pdf);

		// 2. Invitee 1 (AADHAAR)
		JSONObject aadhaarConfig = new JSONObject().put("authTypes", new JSONArray().put("OTP").put("BIO").put("IRIS"));

		JSONObject aadhaarSignature = new JSONObject().put("type", "AADHAAR").put("config", aadhaarConfig);

		JSONObject invitee1 = new JSONObject().put("name", borrowerObj.getFullName())
				.put("email", borrowerObj.getEmailId()).put("emailNotification", true).put("phoneNotification", true)
				.put("signatures", new JSONArray().put(aadhaarSignature));

		// 3. Invitee 2 (AUTOMATED_SIGN)
		JSONObject automatedConfig = new JSONObject().put("id", sign_id).put("passkey", passKey);

		JSONObject automatedSignature = new JSONObject().put("type", "AUTOMATED_SIGN").put("config", automatedConfig);

		JSONObject invitee2 = new JSONObject().put("name", signerName).put("email", signerEmail).put("signatures",
				new JSONArray().put(automatedSignature));

		JSONArray inviteesArray = new JSONArray().put(invitee1).put(invitee2);

		// 4. eSign Priority
		JSONObject eSignPriorityConfigObj = new JSONObject().put("signatureType", "AADHAAR").put("eSignSubType", "OTP")
				.put("retryAttempts", 2).put("order", 1);

		JSONObject eSignPriorityObj = new JSONObject().put("enableEsignPriority", true).put("eSignPriorityConfig",
				new JSONArray().put(eSignPriorityConfigObj));

		// 5. Root JSON Object
		JSONObject root = new JSONObject().put("file", fileObj).put("invitees", inviteesArray)
				.put("eSignPriority", new JSONArray().put(eSignPriorityObj))
				.put("message", "Please eSign this document").put("expiryDays", 10).put("requestSignOrder", true);

		// 6. Send Request
		Mono<String> response = webClient.post().uri(url).header("X-Auth-Token", legal_token)
				.header("Content-Type", "application/json").bodyValue(root.toString()).retrieve()
				.bodyToMono(String.class).doOnNext(jsonResponse -> log.info("Esign Response JSON: {}", jsonResponse));

		return response.block();
	}

	@Override
	public String verifyPANRequest(PanVerificationRequest request) {
		String url = "https://apis2.mufingreenfinance.com/pan";

		JSONObject panRequest = new JSONObject();
		panRequest.put("panNo", request.getPanNumber());
		panRequest.put("name", request.getNameAsPerPan());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = request.getDateOfBirth().format(formatter);
		panRequest.put("dob", formattedDate);
		panRequest.put("apiSource", "D1");

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class).doOnNext(jsonResponse -> log.info("API Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String aadharOtpApi(AadharVerificationRequestDto request) {
		String url = "https://apis2.mufingreenfinance.com/sure/aadhaar/sentOtp";

		JSONObject panRequest = new JSONObject();
		panRequest.put("aadhaarNumber", request.getAadharNumber());

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Sent OTP Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String aadharOtpApiV2(AadharVerificationRequestDto request) {
		String url = "https://kyc-api.surepass.app/api/v1/digilocker/initialize";

		JSONObject data = new JSONObject();
		data.put("signup_flow", true);
		data.put("logo_url",
				"https://mufingreenfinance.com/wp-content/uploads/2022/08/Mufin-Green-Logo-For-Website-01-01-01-1.png");
		data.put("webhook_url", "https://play.svix.com/in/e_pcK3zfNaCC1atugJpm2YN8vSav8/");
		data.put("skip_main_screen", false);

		JSONObject root = new JSONObject();
		root.put("data", data);

		Mono<String> response = webClient.post().uri(url).header("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTc1NDExNDQ5NSwianRpIjoiMmQwZmRiNzYtN2U2OC00MmU3LWFmNGMtN2MyZGE4NDc5MGJjIiwidHlwZSI6ImFjY2VzcyIsImlkZW50aXR5IjoiZGV2Lm11ZmluZmluYW5jZUBzdXJlcGFzcy5pbyIsIm5iZiI6MTc1NDExNDQ5NSwiZXhwIjoyMzg0ODM0NDk1LCJlbWFpbCI6Im11ZmluZmluYW5jZUBzdXJlcGFzcy5pbyIsInRlbmFudF9pZCI6Im1haW4iLCJ1c2VyX2NsYWltcyI6eyJzY29wZXMiOlsidXNlciJdfX0.O_lCJAsukU8Fk9Tg6f5dAnVMQzhAgJoSjxSwH86rrfw")
				.header("Content-Type", "application/json").bodyValue(root.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Digilocker Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String verifyAadhar(String aadharOtp, String string) {
		String url = "https://apis2.mufingreenfinance.com/sure/aadhaar/verifyOtp";

		JSONObject panRequest = new JSONObject();
		panRequest.put("requestId", string);
		panRequest.put("otp", aadharOtp);

		Mono<String> response = webClient.post().uri(url).header("accessToken", "C8HnJ3wW2RzdANp5")
				.header("Content-Type", "application/json").bodyValue(panRequest.toString()).retrieve()
				.bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Verification Response JSON: {}", jsonResponse));
		return response.block(); // Blocking call to get the response synchronously
	}

	@Override
	public String enach(Borrower borrowerDetails, String string) {
		String url = "https://test.cashfree.com/pg/subscriptions";

		// Creating plan_details JSONObject
		JSONObject planDetails = new JSONObject();
		planDetails.put("plan_amount", borrowerDetails.getEmiAmount());
		planDetails.put("plan_name", "EMI" + borrowerDetails.getBorrowerUid());
		planDetails.put("plan_type", "ON_DEMAND");
		planDetails.put("plan_currency", "INR");
		planDetails.put("plan_max_amount", borrowerDetails.getTotalLoanAmount());

		// Creating subscription_meta JSONObject
		JSONObject subscriptionMeta = new JSONObject();
		subscriptionMeta.put("return_url", enachReturnurl + string);

		// Creating authorization_details JSONObject with payment methods
		JSONObject authorizationDetails = new JSONObject();
		authorizationDetails.put("payment_methods", new JSONArray().put("enach"));

		// Creating customer_details JSONObject
		JSONObject customerDetails = new JSONObject();
		customerDetails.put("customer_name", borrowerDetails.getFullName());
		customerDetails.put("customer_email", borrowerDetails.getEmailId());
		customerDetails.put("customer_phone", borrowerDetails.getMobileNo());

		// Creating the main JSONObject
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("plan_details", planDetails);
		jsonObject.put("subscription_meta", subscriptionMeta);
		jsonObject.put("authorization_details", authorizationDetails);
		jsonObject.put("subscription_id", string);
		jsonObject.put("customer_details", customerDetails);

		log.info("Enach Request JSON: {}", jsonObject.toString());

		// Making API request using WebClient
		Mono<String> response = webClient.post().uri(url)
				.header("x-client-id", "TEST104366776b2914d9d803a4c6650a77663401")
				.header("x-client-secret", "cfsk_ma_test_02f0d3df2993bafb6b49720f458ea27a_79a8fcb3")
				.header("x-cell-id", "cell2").header("x-api-version", "2023-08-01") // Fixed version
				.header("Content-Type", "application/json").bodyValue(jsonObject.toString()).retrieve()
				.bodyToMono(String.class).doOnNext(jsonResponse -> log.info("Enach Response JSON: {}", jsonResponse));

		return response.block(); // Avoid blocking if method is reactive
	}

	@Override
	public void checkAadharStatus(BorrowerAadhaar borrowerAadhaar) {

		String url = "https://kyc-api.surepass.app/api/v1/digilocker/download-aadhaar/"
				+ borrowerAadhaar.getAadharRefId();

		Mono<String> response = webClient.get().uri(url).header("Authorization",
				"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTc1NDExNDQ5NSwianRpIjoiMmQwZmRiNzYtN2U2OC00MmU3LWFmNGMtN2MyZGE4NDc5MGJjIiwidHlwZSI6ImFjY2VzcyIsImlkZW50aXR5IjoiZGV2Lm11ZmluZmluYW5jZUBzdXJlcGFzcy5pbyIsIm5iZiI6MTc1NDExNDQ5NSwiZXhwIjoyMzg0ODM0NDk1LCJlbWFpbCI6Im11ZmluZmluYW5jZUBzdXJlcGFzcy5pbyIsInRlbmFudF9pZCI6Im1haW4iLCJ1c2VyX2NsYWltcyI6eyJzY29wZXMiOlsidXNlciJdfX0.O_lCJAsukU8Fk9Tg6f5dAnVMQzhAgJoSjxSwH86rrfw")
				.header("Content-Type", "application/json").retrieve().bodyToMono(String.class)
				.doOnNext(jsonResponse -> log.info("Aadhar Digilocker Response JSON: {}", jsonResponse));

		String responseString = response.block();
		log.info("Aadhar Digilocker Response JSON: {}", responseString);

		JSONObject jsonObject = new JSONObject(responseString);
		String message = jsonObject.getString("message");
		if (message.equals("Success")) {
			borrowerAadhaar.setIsVerified(true);
			JSONObject data = jsonObject.getJSONObject("data");
			JSONObject document = data.getJSONObject("aadhaar_xml_data");
			borrowerAadhaar.setAadharNumber(document.optString("full_name"));

			Customer customer = customerRepository.findByUid(borrowerAadhaar.getBorrowerUid());

			if (!NameMatch.isNameMatching(customer.getNameAsPerPan(), document.optString("full_name"), 60)) {
				log.info("Name Mismatch in Aadhar {} and Pan {}", document.optString("full_name"),
						customer.getNameAsPerPan());
				return;
			}
			borrowerAadhaar.setNameInAadharCard(document.optString("full_name"));
			borrowerAadhaar.setDateofBirth(document.optString("dob", borrowerAadhaar.getDateofBirth()));
			borrowerAadhaar.setGenderInAadhar(document.optString("gender", borrowerAadhaar.getGenderInAadhar()));
			borrowerAadhaar.setCareOfInAadhar(document.optString("care_of", borrowerAadhaar.getCareOfInAadhar()));
			JSONObject address = document.optJSONObject("address");
			String jsonBody = awsS3Service.uploadJsonToS3(borrowerAadhaar.getBorrowerUid() + "AADHAAR.json",
					responseString);
			borrowerAadhaar.setAadharResponse(jsonBody);
			if (address != null) {
				borrowerAadhaar.setCountryInAadhar(address.optString("country", borrowerAadhaar.getCountryInAadhar()));
				borrowerAadhaar.setStateInAadhar(address.optString("state", borrowerAadhaar.getStateInAadhar()));
				borrowerAadhaar.setDistInAadhar(address.optString("dist", borrowerAadhaar.getDistInAadhar()));
				borrowerAadhaar.setSubdistInAadhar(address.optString("subdist", borrowerAadhaar.getSubdistInAadhar()));
				borrowerAadhaar.setPoInAadhar(address.optString("po", borrowerAadhaar.getPoInAadhar()));
				borrowerAadhaar.setVtcInAadhar(address.optString("vtc", borrowerAadhaar.getVtcInAadhar()));
				borrowerAadhaar.setLocInAadhar(address.optString("loc", borrowerAadhaar.getLocInAadhar()));
				borrowerAadhaar.setStreetInAadhar(address.optString("street", borrowerAadhaar.getStreetInAadhar()));
				borrowerAadhaar.setHouseInAadhar(address.optString("house", borrowerAadhaar.getHouseInAadhar()));
				borrowerAadhaar
						.setLandmarkInAadhar(address.optString("landmark", borrowerAadhaar.getLandmarkInAadhar()));
				borrowerAadhaar.setZipInAadhar(document.optString("zip", borrowerAadhaar.getZipInAadhar()));
			}

			Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository
					.findById(borrowerAadhaar.getBorrowerUid());

			userStatusOpt.get().setAadharVerified(true);
			userVerificationStatusRepository.save(userStatusOpt.get());

			borrowerAadharRepository.save(borrowerAadhaar);
		}

	}

}
