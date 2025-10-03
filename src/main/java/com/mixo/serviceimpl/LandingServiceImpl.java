package com.mixo.serviceimpl;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mixo.config.CustomException;
import com.mixo.dto.AadharVerificationOtpRequestDto;
import com.mixo.dto.AadharVerificationRequestDto;
import com.mixo.dto.BankAccountVerificationRequestDto;
import com.mixo.dto.CustomerUidDto;
import com.mixo.dto.EsignDownloadRequestDto;
import com.mixo.dto.EsignVDto;
import com.mixo.dto.FullKycDto;
import com.mixo.dto.LendingRequestDtoV1;
import com.mixo.dto.LendingRequestDtoV2;
import com.mixo.dto.LoanStatus;
import com.mixo.dto.LoanStatusRequestDto;
import com.mixo.dto.PanVerificationRequest;
import com.mixo.dto.VerificationResponse;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerBank;
import com.mixo.model.BorrowerDoc;
import com.mixo.model.BorrowerNach;
import com.mixo.model.BorrowerPan;
import com.mixo.model.Customer;
import com.mixo.model.EmiBreakUp;
import com.mixo.model.Nbfc;
import com.mixo.model.Product;
import com.mixo.model.UserVerificationStatus;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerBankRepository;
import com.mixo.repository.BorrowerDocRepository;
import com.mixo.repository.BorrowerNachRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.repository.CustomerRepository;
import com.mixo.repository.EmiBreakUpRepository;
import com.mixo.repository.UserVerificationStatusRepository;
import com.mixo.service.ApiService;
import com.mixo.service.AwsS3Service;
import com.mixo.service.LandingService;
import com.mixo.service.NbfcService;
import com.mixo.service.PdfService;
import com.mixo.service.ProductService;
import com.mixo.utils.AlphaNumIdGenerator;
import com.mixo.utils.NameMatch;
import com.mixo.utils.ResponseCodeEnum;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LandingServiceImpl implements LandingService {

	@Autowired
	PdfService pdfService;

	@Value("${esignurl}")
	private String esignurl;

	@Value("${eNachurl}")
	private String eNachurl;

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

	@Value("${legal_download}")
	private String legal_download;

	@Value("${legal_token}")
	private String legal_token;

	@Autowired
	UserVerificationStatusRepository userVerificationStatusRepository;

	@Autowired
	CustomerRepository customerRepository;

	private final Map<String, String> adharOtp = new HashMap<>();

	@Autowired
	ApiService apiService;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Autowired
	BorrowerBankRepository borrowerBankRepository;

	@Autowired
	BorrowerDocRepository borrowerDocRepository;

	@Autowired
	NbfcService nbfcService;

	@Autowired
	ProductService productService;

	@Autowired
	BorrowerRepository borrowerRepository;

	@Autowired
	BorrowerNachRepository borrowerNachRepository;

	@Autowired
	EmiBreakUpRepository emiBreakUpRepository;

	@Override
	public VerificationResponse verifyPan(PanVerificationRequest request) {

		Map<String, String> response = CheckDeduplication(request);
		Map<String, String> responseMap = new HashMap<>();
		if (response.get("status").equals("new")) {

			String panVerified = apiService.verifyPANRequest(request);

			JSONObject jsonObject = new JSONObject(panVerified);

			if (!jsonObject.optString("code").equals("0000")) {
				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "PAN verification failed");
			}
			JSONObject data = jsonObject.optJSONObject("response");

			if (!data.optString("status").equals("valid")) {

				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "PAN verification failed");
			}

			List<String> errors = new ArrayList<>();

			if (!data.optBoolean("nameAsPerPanMatch")) {
				errors.add("PAN verification failed -> Name does not match");
			}

			if (!data.optBoolean("dateOfBirthMatch")) {
				errors.add("PAN verification failed -> Date of Birth does not match");
			}

			if (!errors.isEmpty()) {
				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), errors);
			}

			Customer customer = new Customer();
			customer.setPanNumber(request.getPanNumber());
			customer.setPanIdentifier(request.getMobileNo() + request.getPanNumber());
			customer.setMobileNo(request.getMobileNo());
			customer.setDateOfBirth(request.getDateOfBirth());
			customer.setEmailId(request.getEmailId());
			customer.setGender(request.getGender());
			customer.setMaritalStatus(request.getMaritalStatus());
			customer.setNameAsPerPan(request.getNameAsPerPan());
			customer.setUid(AlphaNumIdGenerator.generateId(20));
			customerRepository.save(customer);

			BorrowerPan borrowerPan = new BorrowerPan();

			borrowerPan.setBorrowerUid(customer.getUid());
			borrowerPan.setPanNumber(request.getPanNumber());
			borrowerPan.setNameInPanNumber(request.getNameAsPerPan());
			borrowerPan.setPanCardFlag(data.optString("status"));

			String jsonBody = awsS3Service.uploadJsonToS3(customer.getUid() + "PAN.json", panVerified);
			borrowerPan.setPanResponse(jsonBody);
			borrowerPanRepository.save(borrowerPan);
			response.put("uid", customer.getUid());
			UserVerificationStatus userVerificationStatus = new UserVerificationStatus();
			userVerificationStatus.setUid(customer.getUid());
			userVerificationStatus.setPanVerified(true);
			userVerificationStatusRepository.save(userVerificationStatus);
			return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), getNextStep(customer.getUid()));

		} else if (response.get("status").equals("duplicate")) {
			responseMap.put("status", "duplicate");
			responseMap.put("uid", response.get("message"));
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), response);
		}

		else if (response.get("status").equals("match")) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), response);
		}
		return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
				ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid request");

	}

	private Map<String, String> CheckDeduplication(PanVerificationRequest request) {
		Map<String, String> response = new HashMap<>();
		Customer customer = customerRepository.findByPanIdentifier(request.getMobileNo() + request.getPanNumber());

		if (customer == null) {

			response.put("status", "new");
			Customer customer2 = customerRepository.findByPanNumber(request.getPanNumber());
			if (customer2 != null) {
				response.put("status", "duplicate");
				response.put("message", "Pan number already exists with uid " + customer2.getUid());
				response.put("uid", customer2.getUid());
			}
			Customer customer3 = customerRepository.findByMobileNo(request.getMobileNo());

			if (customer3 != null) {
				response.put("status", "duplicate");
				response.put("message", "Mobile number already exists with uid " + customer3.getUid());
				response.put("uid", customer3.getUid());
			}
			return response;
		}

		response.put("status", "match");
		response.put("message", "Pan number already exists with uid " + customer.getUid());
		response.put("uid", customer.getUid());
		return response;

	}

	public Map<String, Object> getNextStep(String uid) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(uid);

		if (userStatusOpt.isPresent()) {
			UserVerificationStatus userStatus = userStatusOpt.get();

			if (!userStatus.isPanVerified()) {
				return Map.of("uid", uid, "nextStep", "PAN_VERIFICATION", "status", "pending");
			} else if (!userStatus.isAadharVerified()) {
				return Map.of("uid", uid, "nextStep", "AADHAR_VERIFICATION", "status", "pending");
			} else if (!userStatus.isBankVerified()) {
				return Map.of("uid", uid, "nextStep", "BANK_VERIFICATION", "status", "pending");
			} else if (!userStatus.isImageUploaded()) {
				return Map.of("uid", uid, "nextStep", "UPLOAD_USER_IMAGE", "status", "pending");
			} else {
				return Map.of("uid", uid, "nextStep", "COMPLETED", "status", "verified");
			}
		}

		return Map.of("uid", uid, "nextStep", "PAN_VERIFICATION", "status", "pending");
	}

	@Override
	public VerificationResponse aadharRequest(AadharVerificationRequestDto request) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(request.getUid());

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar already verified");
		}

		Customer customer = customerRepository.findByUid(request.getUid());
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		String aadharVerified = apiService.aadharOtpApi(request);

		log.info("Aadhar OTP Shared: " + aadharVerified);

		JSONObject jsonObject = new JSONObject(aadharVerified);

		if (!jsonObject.optString("code").equals("0000")) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar OTP Not Shared ");
		}
		JSONObject data = jsonObject.optJSONObject("response");

		if (!data.optString("message").equalsIgnoreCase("OTP Sent.")) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar OTP Not Shared -> Aadhar is not valid");
		}

		JSONObject aadharData = data.optJSONObject("data");
		String requestId = aadharData.optString("requestId");
		adharOtp.put(request.getUid(), requestId);

		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(request.getUid());

		if (borrowerAadhaar == null) {
			borrowerAadhaar = new BorrowerAadhaar();
		}
		borrowerAadhaar.setBorrowerUid(request.getUid());
		borrowerAadhaar.setAadharNumber(request.getAadharNumber());
		borrowerAadhaarRepository.save(borrowerAadhaar);

		Map<String, String> aadharDataObj = new HashMap<>();
		aadharDataObj.put("uid", request.getUid());
		aadharDataObj.put("nextStep", "AADHAR_VERIFICATION");
		aadharDataObj.put("status", "pending");
		aadharDataObj.put("message",
				"Your Aadhaar OTP has been sent to your registered mobile number. Please check and verify, proceed to the next step");
		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), aadharDataObj);
	}

	@Override
	public VerificationResponse aadharOtpRequest(@Valid AadharVerificationOtpRequestDto request) {

		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(request.getUid());

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar already verified");
		}

		Customer customer = customerRepository.findByUid(request.getUid());
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		String aadharVerified = apiService.verifyAadhar(request.getAadharOtp(), adharOtp.get(request.getUid()));

		JSONObject jsonObject = new JSONObject(aadharVerified);

		if (!jsonObject.optString("code").equals("0000")) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Otp");
		}
		JSONObject data = jsonObject.optJSONObject("response");

		if (!data.optString("status_code").equalsIgnoreCase("200")) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Otp");
		}
		JSONObject aadharData = data.optJSONObject("data");
		String name = aadharData.optString("full_name");

		if (!NameMatch.isNameMatching(customer.getNameAsPerPan(), name, 60)) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(),
					"Aadhar verification failed -> Name does not match with Pan -- ");
		}

		Map<String, String> aadharDataObj = new HashMap<>();
		aadharDataObj.put("uid", request.getUid());
		aadharDataObj.put("nextStep", "BANK_VERIFICATION");
		aadharDataObj.put("status", "pending");
		aadharDataObj.put("message", "Aadhar verification successful");

		userStatus.setAadharVerified(true);
		userVerificationStatusRepository.save(userStatus);
		String address = updateBorrowerAadhar(customer, aadharVerified);

		aadharDataObj.put("aadharInfo", address);

		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), aadharDataObj);

	}

	private String updateBorrowerAadhar(Customer customer, String aadharVerified) {
		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(customer.getUid());
		JSONObject jsonObject = new JSONObject(aadharVerified);

		JSONObject response = jsonObject.optJSONObject("response");
		JSONObject data = response.optJSONObject("data");

		borrowerAadhaar.setAadharNumber(data.optString("aadhaarNumber", borrowerAadhaar.getAadharNumber()));
		borrowerAadhaar.setNameInAadharCard(data.optString("full_name", borrowerAadhaar.getNameInAadharCard()));
		borrowerAadhaar.setDateofBirth(data.optString("dob", borrowerAadhaar.getDateofBirth()));
		borrowerAadhaar.setGenderInAadhar(data.optString("gender", borrowerAadhaar.getGenderInAadhar()));
		borrowerAadhaar.setCareOfInAadhar(data.optString("care_of", borrowerAadhaar.getCareOfInAadhar()));

		// Update address fields
		JSONObject address = data.optJSONObject("address");
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
			borrowerAadhaar.setLandmarkInAadhar(address.optString("landmark", borrowerAadhaar.getLandmarkInAadhar()));
			borrowerAadhaar.setZipInAadhar(data.optString("zip", borrowerAadhaar.getZipInAadhar()));
		}

		// Save raw Aadhaar response
		String jsonBody = awsS3Service.uploadJsonToS3(borrowerAadhaar.getBorrowerUid() + "AADHAAR.json",
				aadharVerified);
		borrowerAadhaar.setAadharResponse(jsonBody);

		// Save updated BorrowerAadhaar to the database
		borrowerAadhaarRepository.save(borrowerAadhaar);
		return data.toString();

	}

	@Override
	public VerificationResponse bankVerification(@Valid BankAccountVerificationRequestDto bankAccountRequestDto) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository
				.findById(bankAccountRequestDto.getUid());

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (!userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Aadhar First");
		}

		if (userStatus.isBankVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Bank Account Already Verified");
		}

		Customer customer = customerRepository.findByUid(bankAccountRequestDto.getUid());
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("ifsc", bankAccountRequestDto.getIfscCode());
		jsonObject.put("bankAccount", bankAccountRequestDto.getBankAccount());
		jsonObject.put("name", customer.getNameAsPerPan());
		jsonObject.put("phone", customer.getMobileNo());
		jsonObject.put("apiSource", "D1");

		String bankVerified = apiService.verifyBankAccount(jsonObject);

		JSONObject jsonObject1 = new JSONObject(bankVerified);

		if (!jsonObject1.optString("code").equals("SUCCESS")) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Bank Not Verified");
		}
		JSONObject data = jsonObject1.optJSONObject("response");

		if (!data.optBoolean("verified")) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Bank Not Verified");
		}
		String name = data.optString("nameAtBank");

		if (!NameMatch.isNameMatching(customer.getNameAsPerPan(), name, 60)) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(),
					"Bank verification failed -> Name does not match with Pan -- \" + name + \"\"");
		}

		BorrowerBank borrowerBank = new BorrowerBank();
		borrowerBank.setBorrowerUid(customer.getUid());
		borrowerBank.setBankAccountNo(bankAccountRequestDto.getBankAccount());
		borrowerBank.setIfscCode(bankAccountRequestDto.getIfscCode());
		String jsonBody = awsS3Service.uploadJsonToS3(customer.getUid() + "BANK.json", bankVerified);
		borrowerBank.setBankResponse(jsonBody);
		borrowerBank.setAccountType(bankAccountRequestDto.getAccountType());
		borrowerBankRepository.save(borrowerBank);

		Map<String, String> aadharDataObj = new HashMap<>();
		aadharDataObj.put("uid", bankAccountRequestDto.getUid());
		aadharDataObj.put("nextStep", "UPLOAD_USER_IMAGE");
		aadharDataObj.put("status", "pending");
		aadharDataObj.put("message", "Bank Account verification successful");

		userStatus.setBankVerified(true);
		userVerificationStatusRepository.save(userStatus);

		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), aadharDataObj);
	}

	@Override
	public VerificationResponse uploadUserImage(MultipartFile file, String uid) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(uid);

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (!userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Aadhar First");
		}

		if (!userStatus.isBankVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Bank Account First");
		}

		if (userStatus.isImageUploaded()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Image Already Uploaded");
		}

		Customer customer = customerRepository.findByUid(uid);
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}
		if (file.isEmpty()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "No file selected for upload.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.matches("image/(png|jpeg|jpg)")) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(),
					"Invalid file type. Only PNG, JPG, and JPEG are allowed.");
		}

		// Process the image file (e.g., save to disk or database)
		// Save the file (you can implement your file saving logic here)

		// After uploading the image, update the session to indicate that the image
		// upload step is done

		try {
			// Convert file to Base64
			byte[] fileBytes = file.getBytes();
			String base64Image = Base64.getEncoder().encodeToString(fileBytes);

			// Optionally save the Base64 string to the database or log it

			// After uploading the image, update the session to indicate that the image
			// upload step is done

			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(uid);

			if (borrowerDoc == null) {
				borrowerDoc = new BorrowerDoc();
				borrowerDoc.setBorrowerUid(uid);
				String jsonBody = awsS3Service.uploadJsonToS3(uid + "userImage.json", base64Image);
				borrowerDoc.setUserImage(jsonBody);
				borrowerDocRepository.save(borrowerDoc);
			}
			borrowerDoc.setBorrowerUid(uid);
			String jsonBody = awsS3Service.uploadJsonToS3(uid + "userImage.json", base64Image);
			borrowerDoc.setUserImage(jsonBody);
			borrowerDocRepository.save(borrowerDoc);

			userStatus.setImageUploaded(true);
			userVerificationStatusRepository.save(userStatus);

			JSONObject aadharDataObj = new JSONObject();
			aadharDataObj.put("uid", uid);
			aadharDataObj.put("nextStep", "COMPLETED");
			aadharDataObj.put("status", "success");
			aadharDataObj.put("message", "Image uploaded successfully. KYC completed successfully.");
			return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), "Image uploaded successfully.");

		} catch (Exception e) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Failed to process the image file.");
		}
	}

	@Override
	public VerificationResponse lendingRequestV1(@Valid LendingRequestDtoV1 lendingRequestDtoV1) {
		String uid = lendingRequestDtoV1.getUid();
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(uid);

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (!userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Aadhar First");
		}

		if (!userStatus.isBankVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Bank Account First");
		}

		if (!userStatus.isImageUploaded()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Upload Image First");
		}

		Customer customer = customerRepository.findByUid(uid);
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		Borrower borrower = checkValidation(lendingRequestDtoV1, customer);

		borrower.setBorrowerUid(AlphaNumIdGenerator.generateId(18));
		borrower.setLoanStatus(LoanStatus.LOAN_INITIATE);
		borrower.setResponseCode(ResponseCodeEnum.REQUEST_PROCESSED.getCode());
		borrower.setResponseMessage(ResponseCodeEnum.REQUEST_PROCESSED.getMessage());

		borrowerRepository.save(borrower);

		Boolean userConsent = lendingRequestDtoV1.getConsentForCibil();

		if (!userConsent) {

			borrower.setLoanStatus(LoanStatus.LOAN_FAILED);
			borrower.setResponseCode(ResponseCodeEnum.FAILURE.getCode());
			borrower.setResponseMessage("Credit Score request rejected, please provide consent");
			borrowerRepository.save(borrower);
		}

		JSONObject aadharDataObj = new JSONObject();
		aadharDataObj.put("uid", uid);
		aadharDataObj.put("loanStatus", borrower.getLoanStatus());
		aadharDataObj.put("message", borrower.getResponseMessage());
		aadharDataObj.put("loanRefNo", borrower.getLoanAggrement());
		return new VerificationResponse(borrower.getResponseCode(), borrower.getResponseMessage(),
				aadharDataObj.toString());
	}

	private Borrower checkValidation(LendingRequestDtoV1 lendingRequestDtoV1, Customer customer) {
		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(lendingRequestDtoV1.getLenderUid());

		if (nbfc.isEmpty()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LENDER_ID.getMessage(),
					ResponseCodeEnum.INVALID_LENDER_ID.getCode());

		}

		Product product = productService.getProductByProductIdAndUid(lendingRequestDtoV1.getProductId(),
				lendingRequestDtoV1.getLenderUid());
		if (product == null) {

			throw new CustomException(ResponseCodeEnum.INVALID_PRODUCT_ID.getMessage(),
					ResponseCodeEnum.INVALID_PRODUCT_ID.getCode());

		}
		if (!product.getApiFlow().equals("Version 1")) {

			throw new CustomException(ResponseCodeEnum.INVALID_VERSION.getMessage(),
					ResponseCodeEnum.INVALID_VERSION.getCode());

		}

		if (lendingRequestDtoV1.getLoanAmount() > product.getMaxAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (lendingRequestDtoV1.getLoanAmount() < product.getMinAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (lendingRequestDtoV1.getNoOfEmi() > Integer.parseInt(product.getMaxTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (lendingRequestDtoV1.getNoOfEmi() < Integer.parseInt(product.getMinTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (lendingRequestDtoV1.getRateOfInterest() < product.getMinInterestRate()
				|| lendingRequestDtoV1.getRateOfInterest() > product.getMaxInterestRate()) {
			throw new CustomException(ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getMessage(),
					ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getCode());

		}
		if (!(product.getPartnershipEndDate().isAfter(LocalDate.now())
				|| product.getPartnershipStartDate().isBefore(LocalDate.now()))) {
			throw new CustomException(ResponseCodeEnum.PARTNERSHIP_EXPIRED.getMessage(),
					ResponseCodeEnum.PARTNERSHIP_EXPIRED.getCode());

		}
		String loanRefNo = product.getLoanAgreementPrefix() + AlphaNumIdGenerator.generateId(16);
		Borrower borrower = new Borrower();

		borrower.setLoanAggrement(loanRefNo);
		borrower.setLenderUid(lendingRequestDtoV1.getLenderUid());
		borrower.setLenderName(nbfc.get().getNbfcName());
		borrower.setLenderBrandName(nbfc.get().getBrandName());
		borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
		borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
		borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
		borrower.setSchemeId(product.getSchemeId());
		borrower.setFullName(customer.getNameAsPerPan().toUpperCase());
		borrower.setGender(customer.getGender());
		borrower.setEmiFrequency(product.getEmiFrequency());
		borrower.setLoanAmount(lendingRequestDtoV1.getLoanAmount());
		borrower.setEmiTime(lendingRequestDtoV1.getNoOfEmi());
		borrower.setProductId(product.getProductId());
		borrower.setLoanType(product.getLoanType());
		borrower.setEmailId(customer.getEmailId());
		borrower.setMobileNo(customer.getMobileNo());
		borrower.setPurposeOfLoan(lendingRequestDtoV1.getPurposeOfLoan());
		borrower.setMaritalStatus(customer.getMaritalStatus());
		borrower.setTotalSalary(lendingRequestDtoV1.getTotalMonthlySalary() * 12);
		borrower.setDateOfBirth(customer.getDateOfBirth());
		borrower.setCustomerLosId(customer.getUid());

		if (borrower.getEmiFrequency().equalsIgnoreCase("Monthly")) {
			LocalDate date = LocalDate.now();
			LocalDate nextDate = date.plusMonths(1);
			borrower.setEmiDate(nextDate.toString());

		}

		if (borrower.getEmiFrequency().equals("Default_Date")) {
			LocalDate date = LocalDate.now(); // Current date
			int nextDate = product.getDefaultDate(); // e.g., 28 or 15

			// Add two months if nextDate < 30, otherwise add one month
			LocalDate nextDateofEmi = (nextDate < 30) ? date.plusMonths(2).withDayOfMonth(nextDate)
					: date.plusMonths(1).withDayOfMonth(nextDate);

			// Handle edge cases where the day is invalid for the month (e.g., 30th
			// February)
			if (nextDateofEmi.getMonth() != ((nextDate < 30) ? date.plusMonths(2) : date.plusMonths(1)).getMonth()) {
				nextDateofEmi = nextDateofEmi
						.withDayOfMonth(nextDateofEmi.getMonth().length(nextDateofEmi.isLeapYear()));
			}

			borrower.setEmiDate(nextDateofEmi.toString());
		}

		borrower.setEmiRate(lendingRequestDtoV1.getRateOfInterest());
		borrower.setLeadSourceId(product.getLeadSourceId());

		if (product.getLoanType().equals("Personal_Loan")) {
			borrower.setProductTypeId(0);

		}
		if (product.getLoanType().equals("Early_Salary")) {
			borrower.setProductTypeId(0);

		}
		if (product.getLoanType().equals("Business_Loan")) {
			borrower.setProductTypeId(4);

		}

		borrower.setResponseMessage("Loan request accepted, proceed to the next step");
		return borrower;
	}

	@Override
	public VerificationResponse lendingRequestV2(@Valid LendingRequestDtoV2 lendingRequestDtoV2) {
		String uid = lendingRequestDtoV2.getUid();
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(uid);

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (!userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Aadhar First");
		}

		if (!userStatus.isBankVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify Bank Account First");
		}

		if (!userStatus.isImageUploaded()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Upload Image First");
		}

		Customer customer = customerRepository.findByUid(uid);
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		Borrower borrower = checkValidation(lendingRequestDtoV2, customer);
		borrower.setWebhookUrl(lendingRequestDtoV2.getWebhookUrl());

		borrower.setSecondaryAddressLine1(lendingRequestDtoV2.getSecondaryAddressLine1());
		borrower.setSecondaryArea(lendingRequestDtoV2.getSecondaryArea());
		borrower.setSecondaryCity(lendingRequestDtoV2.getSecondaryCity());
		borrower.setSecondaryState(lendingRequestDtoV2.getSecondaryState());
		borrower.setSecondaryPinCode(lendingRequestDtoV2.getSecondaryPinCode());
		borrower.setSecondaryLandmark(lendingRequestDtoV2.getSecondaryLandmark());
		borrower.setEmployerName(lendingRequestDtoV2.getNameOfEmployer());
		borrower.setDesignation(lendingRequestDtoV2.getDesignation());
		borrower.setCustomerLosId(uid);

		if (lendingRequestDtoV2.getUdf1() != null) {
			borrower.setUdf1(lendingRequestDtoV2.getUdf1());

		}
		if (lendingRequestDtoV2.getUdf2() != null) {
			borrower.setUdf2(lendingRequestDtoV2.getUdf2());
		}
		if (lendingRequestDtoV2.getUdf3() != null) {
			borrower.setUdf3(lendingRequestDtoV2.getUdf3());
		}
		if (lendingRequestDtoV2.getUdf4() != null) {
			borrower.setUdf4(lendingRequestDtoV2.getUdf4());
		}
		if (lendingRequestDtoV2.getUdf5() != null) {
			borrower.setUdf5(lendingRequestDtoV2.getUdf5());
		}

		borrower.setBorrowerUid(AlphaNumIdGenerator.generateId(18));
		borrower.setLoanStatus(LoanStatus.LOAN_INITIATE);
		borrower.setResponseCode(ResponseCodeEnum.REQUEST_PROCESSED.getCode());
		borrower.setResponseMessage(ResponseCodeEnum.REQUEST_PROCESSED.getMessage());

		borrowerRepository.save(borrower);

		Boolean userConsent = lendingRequestDtoV2.getConsentForCibil();

		if (!userConsent) {

			borrower.setLoanStatus(LoanStatus.LOAN_FAILED);
			borrower.setResponseCode(ResponseCodeEnum.FAILURE.getCode());
			borrower.setResponseMessage("Credit Score request rejected, please provide consent");
			borrowerRepository.save(borrower);
		}

		Map<String, String> aadharDataObj = new HashMap<>();

		aadharDataObj.put("uid", uid);
		aadharDataObj.put("loanStatus", borrower.getLoanStatus().toString());
		aadharDataObj.put("message", borrower.getResponseMessage());
		aadharDataObj.put("loanRefNo", borrower.getLoanAggrement());
		aadharDataObj.put("borrowerUid", borrower.getBorrowerUid());
		return new VerificationResponse(borrower.getResponseCode(), borrower.getResponseMessage(), aadharDataObj);
	}

	private Borrower checkValidation(@Valid LendingRequestDtoV2 lendingRequestDtoV2, Customer customer) {

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(lendingRequestDtoV2.getLenderUid());

		if (nbfc.isEmpty()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LENDER_ID.getMessage(),
					ResponseCodeEnum.INVALID_LENDER_ID.getCode());

		}

		Product product = productService.getProductByProductIdAndUid(lendingRequestDtoV2.getProductId(),
				lendingRequestDtoV2.getLenderUid());
		if (product == null) {

			throw new CustomException(ResponseCodeEnum.INVALID_PRODUCT_ID.getMessage(),
					ResponseCodeEnum.INVALID_PRODUCT_ID.getCode());

		}
		if (!product.getApiFlow().equals("Version 2")) {

			throw new CustomException(ResponseCodeEnum.INVALID_VERSION.getMessage(),
					ResponseCodeEnum.INVALID_VERSION.getCode());

		}

		if (lendingRequestDtoV2.getLoanAmount() > product.getMaxAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (lendingRequestDtoV2.getLoanAmount() < product.getMinAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (lendingRequestDtoV2.getNoOfEmi() > Integer.parseInt(product.getMaxTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (lendingRequestDtoV2.getNoOfEmi() < Integer.parseInt(product.getMinTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (lendingRequestDtoV2.getRateOfInterest() < product.getMinInterestRate()
				|| lendingRequestDtoV2.getRateOfInterest() > product.getMaxInterestRate()) {
			throw new CustomException(ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getMessage(),
					ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getCode());

		}
		if (!(product.getPartnershipEndDate().isAfter(LocalDate.now())
				|| product.getPartnershipStartDate().isBefore(LocalDate.now()))) {
			throw new CustomException(ResponseCodeEnum.PARTNERSHIP_EXPIRED.getMessage(),
					ResponseCodeEnum.PARTNERSHIP_EXPIRED.getCode());

		}
		String loanRefNo = product.getLoanAgreementPrefix() + AlphaNumIdGenerator.generateId(16);
		Borrower borrower = new Borrower();

		borrower.setLoanAggrement(loanRefNo);
		borrower.setLenderUid(lendingRequestDtoV2.getLenderUid());
		borrower.setLenderName(nbfc.get().getNbfcName());
		borrower.setLenderBrandName(nbfc.get().getBrandName());
		borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
		borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
		borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
		borrower.setSchemeId(product.getSchemeId());
		borrower.setFullName(customer.getNameAsPerPan().toUpperCase());
		borrower.setGender(customer.getGender());
		borrower.setEmiFrequency(product.getEmiFrequency());
		borrower.setLoanAmount(lendingRequestDtoV2.getLoanAmount());
		borrower.setEmiTime(lendingRequestDtoV2.getNoOfEmi());
		borrower.setProductId(product.getProductId());
		borrower.setLoanType(product.getLoanType());
		borrower.setEmailId(customer.getEmailId());
		borrower.setMobileNo(customer.getMobileNo());
		borrower.setPurposeOfLoan(lendingRequestDtoV2.getPurposeOfLoan());
		borrower.setMaritalStatus(customer.getMaritalStatus());
		borrower.setTotalSalary(lendingRequestDtoV2.getTotalMonthlySalary() * 12);
		borrower.setDateOfBirth(customer.getDateOfBirth());
		borrower.setCustomerLosId(customer.getUid());

		if (borrower.getEmiFrequency().equalsIgnoreCase("Monthly")) {
			LocalDate date = LocalDate.now();
			LocalDate nextDate = date.plusMonths(1);
			borrower.setEmiDate(nextDate.toString());

		}

		borrower.setEmiDate(lendingRequestDtoV2.getDateOfEmi().toString());

		borrower.setEmiRate(lendingRequestDtoV2.getRateOfInterest());
		borrower.setLeadSourceId(product.getLeadSourceId());

		if (product.getLoanType().equals("Personal_Loan")) {
			borrower.setProductTypeId(0);

		}
		if (product.getLoanType().equals("Early_Salary")) {
			borrower.setProductTypeId(0);

		}
		if (product.getLoanType().equals("Business_Loan")) {
			borrower.setProductTypeId(4);

		}

		borrower.setResponseMessage("Loan request accepted, proceed to the next step");
		return borrower;

	}

	@Override
	public VerificationResponse esign(@Valid EsignVDto esignVDto) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(esignVDto.getBorrowerUid());

		Borrower borrowerDetails = borrower.get();

		if (borrowerDetails.getLoanStatus() == LoanStatus.LOAN_SUCCESSFUL) {
			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(borrowerDetails.getBorrowerUid());
			if (borrowerDoc != null) {
				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerDoc.getESignReqId());
			}

			byte[] pdfBytes = pdfService.generateDynamicLetterheadPdf(borrowerDetails.getBorrowerUid());
			String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

			String esign = apiService.esignV2(borrowerDetails, base64Pdf);
			log.info("esign response : " + esign);

			JSONObject root = new JSONObject(esign);

			JSONObject data = root.getJSONObject("data");

			// Data-level fields
			String documentId = data.getString("documentId");

			JSONArray invitations = data.getJSONArray("invitations");

			// Since only one invitation exists
			JSONObject invite = invitations.getJSONObject(0);
			String signUrl = invite.getString("signUrl");

			BorrowerDoc borrowerDoc1 = new BorrowerDoc();
			borrowerDoc1.setAccessToken(documentId);
			borrowerDoc1.setBorrowerUid(borrowerDetails.getBorrowerUid());
			borrowerDoc1.setMobileNumber(borrowerDetails.getMobileNo());
			borrowerDoc1.setDigioId(signUrl);
			borrowerDoc1.setESignRedirectUrl(esignVDto.getReturnUrl());
			borrowerDoc1.setESignReqId(esignurl + borrowerDetails.getBorrowerUid());

			borrowerDocRepository.save(borrowerDoc1);

			return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerDoc1.getESignReqId());

		}
		return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
				ResponseCodeEnum.BAD_REQUEST.getMessage(),
				"Please check the loan status and try again once the loan is approved.");
	}

	@Override
	public VerificationResponse enach(@Valid EsignVDto esignVDto) {
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(esignVDto.getBorrowerUid());

		Borrower borrowerDetails = borrower.get();

		if (borrowerDetails.getLoanStatus() == LoanStatus.LOAN_SUCCESSFUL) {
			String sessionId = AlphaNumIdGenerator.generateId(16);
			String esign = apiService.enach(borrowerDetails, sessionId);

			JSONObject jsonObject = new JSONObject(esign);

			if (jsonObject.has("subscription_session_id")) {

				List<BorrowerNach> borrowerNachs = borrowerNachRepository
						.findByBorrowerUid(borrowerDetails.getBorrowerUid());
				for (BorrowerNach borrowerNach : borrowerNachs) {
					if (borrowerNach.isNachStatus()) {
						return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
								ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerNach.getNachUrl());
					}

				}

				if (!borrowerNachs.isEmpty()) {
					borrowerNachRepository.deleteAll(borrowerNachs);
				}
				JSONObject plan = jsonObject.optJSONObject("plan_details");
				BorrowerNach borrowerNach = new BorrowerNach();
				borrowerNach.setBorrowerUid(borrowerDetails.getBorrowerUid());
				borrowerNach.setSubscriptionId(sessionId);
				borrowerNach.setPlanName(plan.optString("plan_name"));
				borrowerNach.setPlanRecurringAmount(plan.optDouble("plan_recurring_amount"));
				borrowerNach.setPlanMaxAmount(plan.optDouble("plan_max_amount"));
				borrowerNach.setSubscriptionSessionId(jsonObject.optString("subscription_session_id"));
				borrowerNach.setSubscriptionStatus(jsonObject.optString("subscription_status"));
				borrowerNach.setNachUrl(eNachurl + sessionId);
				borrowerNach.setNachReturnUrl(esignVDto.getReturnUrl());
				borrowerNachRepository.save(borrowerNach);

				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerNach.getNachUrl());
			}

		}
		return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
				ResponseCodeEnum.BAD_REQUEST.getMessage(), "BAD_REQUEST");
	}

	@Override
	public VerificationResponse getCustomerUid(@Valid CustomerUidDto request) {

		Customer customer = customerRepository.findByPanIdentifier(request.getMobileNo() + request.getPanNumber());
		if (customer != null) {
			return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), customer.getUid());
		}
		return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
				ResponseCodeEnum.BAD_REQUEST.getMessage(), "Not Found");
	}

	@Override
	public VerificationResponse getLoanStatus(@Valid LoanStatusRequestDto request) {
		Borrower borrower = borrowerRepository.findByCustomerLosIdAndBorrowerUid(request.getCustomerUid(),
				request.getBorrowerUid());

		if (borrower == null) {
			return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
					ResponseCodeEnum.BAD_REQUEST.getMessage(), "Borrower not found");
		}

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("borrowerUid", borrower.getBorrowerUid());
		responseMap.put("loanStatus", borrower.getLoanStatus());
		responseMap.put("customerUid", borrower.getCustomerLosId());

		if (borrower.getLoanStatus() == LoanStatus.LOAN_SUCCESSFUL) {
			responseMap.put("loanAmount", borrower.getLoanAmount());

			List<EmiBreakUp> breakUps = emiBreakUpRepository.findByBorrowerUid(borrower.getBorrowerUid());
			List<Map<String, Object>> emiList = new ArrayList<>();

			for (EmiBreakUp emi : breakUps) {
				Map<String, Object> emiDetails = new HashMap<>();
				emiDetails.put("emiAmount", emi.getDueAmount());
				emiDetails.put("emiDate", emi.getDueDate());
				emiDetails.put("emiNumber", emi.getInstallmentNo());
				emiList.add(emiDetails);
			}

			responseMap.put("emiBreakUp", emiList);
		}

		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), responseMap);
	}

	@Override
	public VerificationResponse fullKyc(@Valid FullKycDto fullKycDto) {

		Map<String, String> responseMap = new HashMap<>();
		Map<String, String> response = CheckDeduplicationCheck(fullKycDto);

		if (response.get("status").equals("duplicate")) {
			responseMap.put("status", "duplicate");
			responseMap.put("uid", response.get("message"));
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), response);
		}
		if (response.get("status").equals("match")) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), response);
		}
		Customer customer = new Customer();
		customer.setPanNumber(fullKycDto.getPanNumber());
		customer.setPanIdentifier(fullKycDto.getMobileNo() + fullKycDto.getPanNumber());
		customer.setMobileNo(fullKycDto.getMobileNo());
		customer.setDateOfBirth(fullKycDto.getDateOfBirth());
		customer.setEmailId(fullKycDto.getEmailId());
		customer.setGender(fullKycDto.getGender());
		customer.setMaritalStatus(fullKycDto.getMaritalStatus());
		customer.setNameAsPerPan(fullKycDto.getNameAsPerPan());
		customer.setUid(AlphaNumIdGenerator.generateId(20));
		customerRepository.save(customer);

		BorrowerPan borrowerPan = new BorrowerPan();

		borrowerPan.setBorrowerUid(customer.getUid());
		borrowerPan.setPanNumber(fullKycDto.getPanNumber());
		borrowerPan.setNameInPanNumber(fullKycDto.getNameAsPerPan());
		borrowerPan.setPanCardFlag("Y");

		String jsonBody = awsS3Service.uploadJsonToS3(customer.getUid() + "PAN.json", fullKycDto.toString());
		borrowerPan.setPanResponse(jsonBody);
		borrowerPanRepository.save(borrowerPan);

		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(customer.getUid());

		if (borrowerAadhaar == null) {
			borrowerAadhaar = new BorrowerAadhaar();
		}
		borrowerAadhaar.setAadharNumber(fullKycDto.getAadharNumber());
		borrowerAadhaar.setNameInAadharCard(fullKycDto.getNameInAadharCard());
		borrowerAadhaar.setDateofBirth(fullKycDto.getDateOfBirth().toString());
		borrowerAadhaar.setGenderInAadhar(fullKycDto.getGenderInAadhar());
		borrowerAadhaar.setCareOfInAadhar(fullKycDto.getCareOfInAadhar());

		// Update address fields
		borrowerAadhaar.setCountryInAadhar(fullKycDto.getCountryInAadhar());
		borrowerAadhaar.setStateInAadhar(fullKycDto.getStateInAadhar());
		borrowerAadhaar.setDistInAadhar(fullKycDto.getDistInAadhar());
		borrowerAadhaar.setSubdistInAadhar(fullKycDto.getSubdistInAadhar());
		borrowerAadhaar.setPoInAadhar(fullKycDto.getPoInAadhar());
		borrowerAadhaar.setVtcInAadhar(fullKycDto.getVtcInAadhar());
		borrowerAadhaar.setLocInAadhar(fullKycDto.getLocInAadhar());
		borrowerAadhaar.setStreetInAadhar(fullKycDto.getStreetInAadhar());
		borrowerAadhaar.setHouseInAadhar(fullKycDto.getHouseInAadhar());
		borrowerAadhaar.setLandmarkInAadhar(fullKycDto.getLandmarkInAadhar());
		borrowerAadhaar.setZipInAadhar(fullKycDto.getZipInAadhar());

		// Save raw Aadhaar response
		borrowerAadhaar.setBorrowerUid(customer.getUid());
		borrowerAadhaar.setAadharNumber(fullKycDto.getAadharNumber());
		borrowerAadhaarRepository.save(borrowerAadhaar);

		BorrowerBank borrowerBank = new BorrowerBank();
		borrowerBank.setBorrowerUid(customer.getUid());
		borrowerBank.setBankAccountNo(fullKycDto.getBankAccount());
		borrowerBank.setIfscCode(fullKycDto.getIfscCode());
		borrowerBank.setBankResponse(jsonBody);
		borrowerBank.setAccountType(fullKycDto.getAccountType());
		borrowerBankRepository.save(borrowerBank);

		UserVerificationStatus userVerificationStatus = new UserVerificationStatus();
		userVerificationStatus.setUid(customer.getUid());
		userVerificationStatus.setPanVerified(true);
		userVerificationStatus.setAadharVerified(true);
		userVerificationStatus.setBankVerified(true);
		userVerificationStatus.setImageUploaded(true);
		userVerificationStatusRepository.save(userVerificationStatus);
		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), getNextStep(customer.getUid()));
	}

	private Map<String, String> CheckDeduplicationCheck(@Valid FullKycDto fullKycDto) {
		Map<String, String> response = new HashMap<>();
		Customer customer = customerRepository
				.findByPanIdentifier(fullKycDto.getMobileNo() + fullKycDto.getPanNumber());

		if (customer == null) {

			response.put("status", "new");
			Customer customer2 = customerRepository.findByPanNumber(fullKycDto.getPanNumber());
			if (customer2 != null) {
				response.put("status", "duplicate");
				response.put("message", "Pan number already exists with uid " + customer2.getUid());
				response.put("uid", customer2.getUid());
			}
			Customer customer3 = customerRepository.findByMobileNo(fullKycDto.getMobileNo());

			if (customer3 != null) {
				response.put("status", "duplicate");
				response.put("message", "Mobile number already exists with uid " + customer3.getUid());
				response.put("uid", customer3.getUid());
			}
			return response;
		}

		response.put("status", "match");
		response.put("message", "Pan number already exists with uid " + customer.getUid());
		response.put("uid", customer.getUid());
		return response;
	}

	@Override
	public byte[] getEsign(@Valid EsignDownloadRequestDto request) {
		BorrowerDoc esign = borrowerDocRepository.findByBorrowerUid(request.getBorrowerUid());
		if (esign == null) {
			return null;
		}
		if (!esign.isESignStatus()) {
			return null;
		}
		try {

			String digio_url = "https://api.digio.in/v2/client/document/download?document_id=";
			String digio_token = "QUkyRVpTUUYxUUdHRDFMWE9DNkxaMUo5U1NQODhZTVM6UUoyNE1aS0M4OUpPWFBWU1YyR0lHQjhHNUxNMzdVOE8=";
			HttpClient client = HttpClient.newHttpClient();
			String url = digio_url + esign.getDigioId();

			HttpRequest requests = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json")
					.header("content-type", "application/json").header("Authorization", "Basic " + digio_token).GET()
					.build();

			HttpResponse<byte[]> response = client.send(requests, HttpResponse.BodyHandlers.ofByteArray());

			if (response.statusCode() == 200) {
				byte[] pdfBytes = response.body();

				return pdfBytes;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public VerificationResponse uploadEsignedPdf(MultipartFile file, String borrowerId) {

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerId);

		Borrower borrowerDetails = borrower.get();

		if (borrowerDetails.getLoanStatus() == LoanStatus.LOAN_SUCCESSFUL) {
			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(borrowerDetails.getBorrowerUid());
			if (borrowerDoc != null && borrowerDoc.getESignReqId() != null) {
				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerDoc.getESignReqId());
			}

			String contentType = file.getContentType();
			if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid file type. Only PDF is allowed.");
			}

			try {
				// Convert file to Base64
				byte[] fileBytes = file.getBytes();
				String base64Image = Base64.getEncoder().encodeToString(fileBytes);
				String esign = apiService.esignV2forDirectSign(borrowerDetails, base64Image);
				JSONObject root = new JSONObject(esign);

				JSONObject data = root.getJSONObject("data");

				// Data-level fields
				String documentId = data.getString("documentId");

				JSONArray invitations = data.getJSONArray("invitations");

				// Since only one invitation exists
				JSONObject invite = invitations.getJSONObject(0);
				String signUrl = invite.getString("signUrl");

				String url = legal_download + documentId;

				HttpClient client = HttpClient.newHttpClient();

				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json")
						.header("content-type", "application/json").header("X-Auth-Token", legal_token).GET().build();

				HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

				String base64Pdf = Base64.getEncoder().encodeToString(response.body());

				if (borrowerDoc == null) {
					borrowerDoc = new BorrowerDoc();
					borrowerDoc.setBorrowerUid(borrowerId);
					String jsonBody = awsS3Service.uploadJsonToS3(borrowerId + "esigned.json", base64Pdf);
					borrowerDoc.setESignReqId(jsonBody);
					borrowerDocRepository.save(borrowerDoc);
				}
				borrowerDoc.setBorrowerUid(borrowerId);
				String jsonBody = awsS3Service.uploadJsonToS3(borrowerId + "esigned.json", base64Pdf);
				borrowerDoc.setESignReqId(jsonBody);
				borrowerDocRepository.save(borrowerDoc);

				JSONObject documentUpload = createJsonForDocx(borrowerDetails.getFinanceId(), base64Pdf, "kfs");
				String respom = callApiForDocUploadCreation(documentUpload);
				callApiForDocUploadWithHmac(respom, documentUpload);

				JSONObject aadharDataObj = new JSONObject();
				aadharDataObj.put("borrowerId", borrowerId);
				aadharDataObj.put("status", "success");
				aadharDataObj.put("message", "File uploaded successfully.");
				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), "File uploaded successfully.");

			} catch (Exception e) {

				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Failed to process the PDF file.");
			}
		}
		return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
				ResponseCodeEnum.BAD_REQUEST.getMessage(),
				"Please check the loan status and try again once the loan is approved.");

	}

	private void callApiForDocUploadWithHmac(String respom, JSONObject documentUpload) {
		try {

			URL url = new URL(docUploadUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", respom.replaceAll("\"", ""));

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(documentUpload.toString().trim());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				log.info("All Cloud DocUpload  Api Response= " + response);

				JSONObject obj = new JSONObject(response);
				log.info("All Cloud DocUpload  Api Response= " + obj);

//				System.out.println("All Cloud DocUpload  Api Response= " + responseStream.);
//				System.out.println("All Cloud DocUpload  Api Response= " + response);
			}
		} catch (Exception e) {
		}

	}

	private String callApiForDocUploadCreation(JSONObject documentUpload) {
		try {

//			log.info("callApiForDocUploadCreation : " + documentUpload.toString());

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
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			String response = s.hasNext() ? s.next() : "";
			return response;
		} catch (Exception e) {
		}
		return "";

	}

	private JSONObject createJsonForDocx(int financeId, String base64Pdf, String string) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("EntityId", financeId);
		jsonObject.put("EntityTypeId", "Finance");
		JSONObject UploadDocumentDTO = new JSONObject();
		UploadDocumentDTO.put("DocType", "KeyFactSheet");
		UploadDocumentDTO.put("Order", 1);
		UploadDocumentDTO.put("FileName", "SIGNED_kfs" + LocalDate.now() + ".pdf");
		UploadDocumentDTO.put("UploadDocBase64", base64Pdf);
		jsonObject.put("UploadDocumentDTO", UploadDocumentDTO);
		return jsonObject;
	}

	private JSONObject createJsonForLogDocx(int financeId, String base64Pdf, String string) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("EntityId", financeId);
		jsonObject.put("EntityTypeId", "Finance");
		JSONObject UploadDocumentDTO = new JSONObject();
		UploadDocumentDTO.put("DocType", "KYCDocument");
		UploadDocumentDTO.put("Order", 1);
		UploadDocumentDTO.put("FileName", "Kyc" + LocalDate.now() + ".pdf");
		UploadDocumentDTO.put("UploadDocBase64", base64Pdf);
		jsonObject.put("UploadDocumentDTO", UploadDocumentDTO);
		return jsonObject;
	}

	@Override
	public VerificationResponse aadharRequestv2(AadharVerificationRequestDto request) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(request.getUid());

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (userStatus.isAadharVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar already verified");
		}

		Customer customer = customerRepository.findByUid(request.getUid());
		if (null == customer) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");

		}

		String aadharVerified = apiService.aadharOtpApiV2(request);

		log.info("Aadhar OTP Shared: " + aadharVerified);

		JSONObject jsonObject = new JSONObject(aadharVerified);

		if (!jsonObject.optString("message_code").equals("success")) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Aadhar Service Down");
		}

		JSONObject aadharData = jsonObject.optJSONObject("data");

		String requestId = aadharData.optString("client_id");

		String utl = aadharData.optString("url");
		adharOtp.put(request.getUid(), requestId);

		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(request.getUid());

		if (borrowerAadhaar == null) {
			borrowerAadhaar = new BorrowerAadhaar();
		}
		borrowerAadhaar.setBorrowerUid(request.getUid());
		borrowerAadhaar.setAadharNumber(request.getAadharNumber());
		borrowerAadhaar.setAadharRefId(requestId);
		borrowerAadhaar.setIsVerified(false);
		borrowerAadhaarRepository.save(borrowerAadhaar);

		Map<String, String> aadharDataObj = new HashMap<>();
		aadharDataObj.put("uid", request.getUid());
		aadharDataObj.put("nextStep", "BANK_VERIFICATION");
		aadharDataObj.put("status", "pending");
		aadharDataObj.put("verficationLink", utl);

		return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), aadharDataObj);
	}

	@Override
	public VerificationResponse aadharStatusRequestv2(@Valid AadharVerificationRequestDto request) {
		Optional<UserVerificationStatus> userStatusOpt = userVerificationStatusRepository.findById(request.getUid());

		if (!userStatusOpt.isPresent()) {

			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid Uid");
		}
		UserVerificationStatus userStatus = userStatusOpt.get();
		if (!userStatus.isPanVerified()) {
			return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Verify PAN first");
		}
		if (userStatus.isAadharVerified()) {
			Map<String, Object> data = new HashMap<>();

			BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(request.getUid());
			if (borrowerAadhaar != null) {
				data.put("message", "Aadhar already verified");
				data.put("name", borrowerAadhaar.getNameInAadharCard());
				data.put("dob", borrowerAadhaar.getDateofBirth());
				data.put("gender", borrowerAadhaar.getGenderInAadhar());
				data.put("houseNo", borrowerAadhaar.getHouseInAadhar());
				data.put("street", borrowerAadhaar.getStreetInAadhar());
				data.put("locInAadhar", borrowerAadhaar.getLocInAadhar());
				data.put("vtcInAadhar", borrowerAadhaar.getVtcInAadhar());
				data.put("districtInAadhar", borrowerAadhaar.getDistInAadhar());
				data.put("stateInAadhar", borrowerAadhaar.getStateInAadhar());
				data.put("pincodeInAadhar", borrowerAadhaar.getZipInAadhar());
			} else {
				data.put("message", "Aadhar not found");
			}

			return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), data);

		}
		if (!userStatus.isAadharVerified()) {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("message", "Aadhar not verified or try after 1 min");

			return new VerificationResponse(ResponseCodeEnum.NO_RESULT.getCode(),
					ResponseCodeEnum.NO_RESULT.getMessage(), jsonObject);

		}
		return null;
	}

	@Override
	public VerificationResponse uploadLogPdf(MultipartFile file, String borrowerId) {
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerId);

		Borrower borrowerDetails = borrower.get();

		if (borrowerDetails.getLoanStatus() == LoanStatus.LOAN_SUCCESSFUL) {
			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(borrowerDetails.getBorrowerUid());
			if (borrowerDoc != null && borrowerDoc.getPanImage() != null) {
				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), borrowerDoc.getPanImage());
			}

			String contentType = file.getContentType();
			if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Invalid file type. Only PDF is allowed.");
			}

			try {
				// Convert file to Base64
				byte[] fileBytes = file.getBytes();
				String base64Image = Base64.getEncoder().encodeToString(fileBytes);

				if (borrowerDoc == null) {
					borrowerDoc = new BorrowerDoc();
					borrowerDoc.setBorrowerUid(borrowerId);
					String jsonBody = awsS3Service.uploadJsonToS3(borrowerId + "log.json", base64Image);
					borrowerDoc.setPanImage(jsonBody);
					borrowerDocRepository.save(borrowerDoc);
				}
				borrowerDoc.setBorrowerUid(borrowerId);
				String jsonBody = awsS3Service.uploadJsonToS3(borrowerId + "log.json", base64Image);
				borrowerDoc.setPanImage(jsonBody);
				borrowerDocRepository.save(borrowerDoc);

				JSONObject documentUpload = createJsonForLogDocx(borrowerDetails.getFinanceId(), base64Image, "kfs");
				String respom = callApiForDocUploadCreation(documentUpload);
				callApiForDocUploadWithHmac(respom, documentUpload);

				JSONObject aadharDataObj = new JSONObject();
				aadharDataObj.put("borrowerId", borrowerId);
				aadharDataObj.put("status", "success");
				aadharDataObj.put("message", "File uploaded successfully.");
				return new VerificationResponse(ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
						ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), "File uploaded successfully.");

			} catch (Exception e) {

				return new VerificationResponse(ResponseCodeEnum.INVALID_REQUEST.getCode(),
						ResponseCodeEnum.INVALID_REQUEST.getMessage(), "Failed to process the PDF file.");
			}
		}
		return new VerificationResponse(ResponseCodeEnum.BAD_REQUEST.getCode(),
				ResponseCodeEnum.BAD_REQUEST.getMessage(),
				"Please check the loan status and try again once the loan is approved.");

	}

}
