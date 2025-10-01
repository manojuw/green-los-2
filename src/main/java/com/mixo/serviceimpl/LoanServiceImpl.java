package com.mixo.serviceimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mixo.config.CustomException;
import com.mixo.dto.AadharBorrowerDto;
import com.mixo.dto.AadharOtpRequestDto;
import com.mixo.dto.AadharRequestDto;
import com.mixo.dto.BankAccountRequestDto;
import com.mixo.dto.BankBorrowerDto;
import com.mixo.dto.CibilBorrowerDto;
import com.mixo.dto.CibilRequestDto;
import com.mixo.dto.ConfigureDto;
import com.mixo.dto.DocBorrowerDto;
import com.mixo.dto.EsignDto;
import com.mixo.dto.ExtraInfoDto;
import com.mixo.dto.LoanRequestDto;
import com.mixo.dto.LoanRequestDtoV2;
import com.mixo.dto.LoanRequestDtoV3;
import com.mixo.dto.LoanResponseDto;
import com.mixo.dto.LoanStatus;
import com.mixo.dto.PANRequestDto;
import com.mixo.dto.PanBorrowerDto;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;
import com.mixo.model.BorrowerBank;
import com.mixo.model.BorrowerCibil;
import com.mixo.model.BorrowerDoc;
import com.mixo.model.BorrowerPan;
import com.mixo.model.JourneyEngine;
import com.mixo.model.Nbfc;
import com.mixo.model.Product;
import com.mixo.repository.BorrowerAadhaarRepository;
import com.mixo.repository.BorrowerBankRepository;
import com.mixo.repository.BorrowerCibilRepository;
import com.mixo.repository.BorrowerDocRepository;
import com.mixo.repository.BorrowerPanRepository;
import com.mixo.repository.BorrowerRepository;
import com.mixo.service.AllCloudApiService;
import com.mixo.service.ApiService;
import com.mixo.service.AwsS3Service;
import com.mixo.service.JourneyEngineService;
import com.mixo.service.LoanService;
import com.mixo.service.NbfcService;
import com.mixo.service.ProductService;
import com.mixo.utils.AlphaNumIdGenerator;
import com.mixo.utils.CommonResponse;
import com.mixo.utils.NameMatch;
import com.mixo.utils.ResponseCodeEnum;
import com.mixo.utils.ValidatedRequest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {

	private final Map<String, LoanResponseDto> sessionStore = new HashMap<>();

	private final Map<String, String> adharOtp = new HashMap<>();

	private static final long MAX_PDF_SIZE = 2 * 1024 * 1024;

	// Allowed next steps
	private static final String PAN_API_STEP = "PAN_API_STEP";
	private static final String CREATE_LOAN_API = "CREATE_LOAN_API";
	private static final String AADHAR_API_STEP = "AADHAR_API_STEP";
	private static final String AADHAR_OTP_API_STEP = "AADHAR_OTP_API_STEP";
	private static final String PENNY_DROP_API_STEP = "PENNY_DROP_API_STEP";
	private static final String CIBIL_API_STEP = "CIBIL_API_STEP";
	private static final String USER_IMAGE_API_STEP = "USER_IMAGE_API_STEP";
	private static final String PAN_IMAGE_API_STEP = "PAN_IMAGE_API_STEP";
	private static final String AADHAR_IMAGE_API_STEP = "AADHAR_IMAGE_API_STEP";
	private static final String BANK_DOC_API_STEP = "BANK_DOC_API_STEP";
	private static final String PROCESS_LOAN_API = "PROCESS_LOAN_API";
	private static final String STEP_COMPLETE = "STEP_COMPLETE";

	@Autowired
	NbfcService nbfcService;

	@Autowired
	ProductService productService;

	@Autowired
	BorrowerRepository borrowerRepository;

	@Autowired
	BorrowerBankRepository borrowerBankRepository;

	@Autowired
	ApiService apiService;

	@Autowired
	JourneyEngineService journeyEngineService;

	@Autowired
	AwsS3Service awsS3Service;

	@Autowired
	BorrowerPanRepository borrowerPanRepository;

	@Autowired
	BorrowerAadhaarRepository borrowerAadhaarRepository;

	@Autowired
	BorrowerCibilRepository borrowerCibilRepository;

	@Autowired
	BorrowerDocRepository borrowerDocRepository;

	@Autowired
	AllCloudApiService allCloudApiService;

	@Override
	public CommonResponse createLoan(@Valid LoanRequestDto loanRequestDto) {
		// Generate a session token

		String sessionToken = AlphaNumIdGenerator.generateId(34);
		long sessionActiveTime = System.currentTimeMillis() + (1440 * 15 * 60 * 1000); // 15 Days

		// Simulated loan reference number
		Borrower borrower = checkValidation(loanRequestDto);

		// Determine the first nextStep
		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrower.getLenderUid());
		configureDto.setProductId(loanRequestDto.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(CREATE_LOAN_API, journeyEngine);
		borrower.setNextStep(nextStep);
		borrower.setBorrowerUid(sessionToken);
		borrower.setLoanStatus(LoanStatus.LOAN_INITIATE);

		// Persist the session
		borrowerRepository.save(borrower);

		// Prepare the response
		LoanResponseDto response = new LoanResponseDto();
		response.setMessage("Loan request accepted, proceed to the next step");
		response.setLoanRefNo(borrower.getLoanAggrement());
		response.setNextStep(nextStep);
		response.setSessionToken(sessionToken);
		response.setSessionActiveTime(sessionActiveTime);

		awsS3Service.uploadJsonToS3(sessionToken + "loanRequest.json", loanRequestDto.toString());

		// Store session data
		sessionStore.put(sessionToken, response);
		CommonResponse responsesj = new CommonResponse(HttpStatus.OK.value(),
				ResponseCodeEnum.REQUEST_PROCESSED.getCode(), ResponseCodeEnum.REQUEST_PROCESSED.getMessage(),
				response);
		return responsesj;
	}

	@Override
	public CommonResponse createLoanV2(@Valid LoanRequestDtoV2 loanRequestDto) {
		String sessionToken = AlphaNumIdGenerator.generateId(34);
		long sessionActiveTime = System.currentTimeMillis() + (1440 * 15 * 60 * 1000); // 15 Days

		// Simulated loan reference number
		Borrower borrower = checkValidationV2(loanRequestDto);

		// Determine the first nextStep
		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrower.getLenderUid());
		configureDto.setProductId(loanRequestDto.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(CREATE_LOAN_API, journeyEngine);
		borrower.setNextStep(nextStep);
		borrower.setBorrowerUid(sessionToken);
		borrower.setLoanStatus(LoanStatus.LOAN_INITIATE);

		// Persist the session
		borrowerRepository.save(borrower);

		// Prepare the response
		LoanResponseDto response = new LoanResponseDto();
		response.setMessage("Loan request accepted, proceed to the next step");
		response.setLoanRefNo(borrower.getLoanAggrement());
		response.setNextStep(nextStep);
		response.setSessionToken(sessionToken);
		response.setSessionActiveTime(sessionActiveTime);

		awsS3Service.uploadJsonToS3(sessionToken + "loanRequest.json", loanRequestDto.toString());

		// Store session data
		sessionStore.put(sessionToken, response);
		CommonResponse responsesj = new CommonResponse(HttpStatus.OK.value(),
				ResponseCodeEnum.REQUEST_PROCESSED.getCode(), ResponseCodeEnum.REQUEST_PROCESSED.getMessage(),
				response);
		return responsesj;
	}

	@Override
	public CommonResponse createLoanV3(@Valid LoanRequestDtoV3 loanRequestDto) {

		String sessionToken = AlphaNumIdGenerator.generateId(34);
		long sessionActiveTime = System.currentTimeMillis() + (1440 * 15 * 60 * 1000); // 15 Day

		// Simulated loan reference number
		Borrower borrower = checkValidationV3(loanRequestDto);
		String nextStep = "";
		if (borrower.getNextStep() != null) {

			// Determine the first nextStep

			nextStep = borrower.getNextStep();

			BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(borrower.getBorrowerUid());
			BorrowerAadhaar borrowerAadhar = borrowerAadhaarRepository.findByBorrowerUid(borrower.getBorrowerUid());
			BorrowerBank borrowerBankAccount = borrowerBankRepository.findByBorrowerUid(borrower.getBorrowerUid());
			BorrowerCibil borrowerCibil = borrowerCibilRepository.findByBorrowerUid(borrower.getBorrowerUid());
			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(borrower.getBorrowerUid());
			borrowerPan.setBorrowerUid(sessionToken);
			borrowerAadhar.setBorrowerUid(sessionToken);
			borrowerBankAccount.setBorrowerUid(sessionToken);
			borrowerCibil.setBorrowerUid(sessionToken);
			borrowerDoc.setBorrowerUid(sessionToken);
			borrowerDoc.setDigioId(nextStep);
			borrowerDoc.setESignRedirectUrl(null);
			borrowerDoc.setESignStatus(false);

			borrowerPanRepository.save(borrowerPan);
			borrowerAadhaarRepository.save(borrowerAadhar);
			borrowerBankRepository.save(borrowerBankAccount);
			borrowerCibilRepository.save(borrowerCibil);
			borrowerDocRepository.save(borrowerDoc);

		} else {
			ConfigureDto configureDto = new ConfigureDto();
			configureDto.setUid(borrower.getLenderUid());
			configureDto.setProductId(loanRequestDto.getProductId());
			// Start the journey
			JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
			nextStep = provideNextStep(CREATE_LOAN_API, journeyEngine);

		}
		borrower.setNextStep(nextStep);
		borrower.setBorrowerUid(sessionToken);
		borrower.setLoanStatus(LoanStatus.LOAN_INITIATE);
		// Persist the session
		borrowerRepository.save(borrower);

		// Prepare the response
		LoanResponseDto response = new LoanResponseDto();
		response.setMessage("Loan request accepted, proceed to the next step");
		response.setLoanRefNo(borrower.getLoanAggrement());
		response.setNextStep(nextStep);
		response.setSessionToken(sessionToken);
		response.setSessionActiveTime(sessionActiveTime);

		awsS3Service.uploadJsonToS3(sessionToken + "loanRequest.json", loanRequestDto.toString());

		// Store session data
		sessionStore.put(sessionToken, response);
		CommonResponse responsesj = new CommonResponse(HttpStatus.OK.value(),
				ResponseCodeEnum.REQUEST_PROCESSED.getCode(), ResponseCodeEnum.REQUEST_PROCESSED.getMessage(),
				response);
		return responsesj;

	}

	private Borrower checkValidationV3(@Valid LoanRequestDtoV3 loanRequestDto) {

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(loanRequestDto.getLenderUid());

		if (nbfc.isEmpty()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LENDER_ID.getMessage(),
					ResponseCodeEnum.INVALID_LENDER_ID.getCode());

		}

		Product product = productService.getProductByProductIdAndUid(loanRequestDto.getProductId(),
				loanRequestDto.getLenderUid());
		if (product == null) {

			throw new CustomException(ResponseCodeEnum.INVALID_PRODUCT_ID.getMessage(),
					ResponseCodeEnum.INVALID_PRODUCT_ID.getCode());

		}
		if (!product.getApiFlow().equals("Version 3")) {

			throw new CustomException(ResponseCodeEnum.INVALID_VERSION.getMessage(),
					ResponseCodeEnum.INVALID_VERSION.getCode());

		}

		if (loanRequestDto.getLoanAmount() > product.getMaxAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getLoanAmount() < product.getMinAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getNoOfEMI() > Integer.parseInt(product.getMaxTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getNoOfEMI() < Integer.parseInt(product.getMinTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getRateOfInterest() <= product.getMinInterestRate()
				|| loanRequestDto.getRateOfInterest() > product.getMaxInterestRate()) {
			throw new CustomException(ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getMessage(),
					ResponseCodeEnum.INVALID_RATE_OF_INTEREST.getCode());

		}
		if (!(product.getPartnershipEndDate().isAfter(LocalDate.now())
				|| product.getPartnershipStartDate().isBefore(LocalDate.now()))) {
			throw new CustomException(ResponseCodeEnum.PARTNERSHIP_EXPIRED.getMessage(),
					ResponseCodeEnum.PARTNERSHIP_EXPIRED.getCode());

		}
		String loanRefNo = product.getLoanAgreementPrefix() + AlphaNumIdGenerator.generateId(16);
		List<Borrower> borrowerList = borrowerRepository
				.findByUserUniqueIdAndLoanStatus(loanRequestDto.getUserUniqueId(), LoanStatus.LOAN_SUCCESSFUL);

		if (!borrowerList.isEmpty()) {
			Borrower borrower1 = borrowerList.get(borrowerList.size() - 1);

			LocalDateTime createdOn = borrower1.getCreatedOn();
			LocalDateTime now = LocalDateTime.now();
			Duration duration = Duration.between(createdOn, now);
			long days = duration.toDays();
			if (days > 15) {
				log.info("User Unique Id expired in 15 days. Days: " + days + " User Unique Id: "
						+ loanRequestDto.getUserUniqueId());
				throw new CustomException(ResponseCodeEnum.EXPIRED_USER_UNIQUE_ID.getMessage(),
						ResponseCodeEnum.EXPIRED_USER_UNIQUE_ID.getCode());

			} else {
				Borrower borrower = new Borrower();
//				BeanUtils.copyProperties(borrower1, borrower);
				borrower.setLoanAggrement(loanRefNo);
				borrower.setUserUniqueId(loanRequestDto.getUserUniqueId());
				borrower.setLenderUid(loanRequestDto.getLenderUid());
				borrower.setLenderName(nbfc.get().getNbfcName());
				borrower.setLenderBrandName(nbfc.get().getBrandName());
				borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
				borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
				borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
				borrower.setSchemeId(product.getSchemeId());
				borrower.setFullName(loanRequestDto.getNameAsPerPan().toUpperCase());
				borrower.setGender(loanRequestDto.getGender());
				borrower.setEmiFrequency(product.getEmiFrequency());
				borrower.setLoanAmount(loanRequestDto.getLoanAmount());
				borrower.setEmiTime(loanRequestDto.getNoOfEMI());
				borrower.setProductId(product.getProductId());
				borrower.setLoanType(product.getLoanType());
				borrower.setEmploymentType(loanRequestDto.getEmploymentType());
				borrower.setEmailId(loanRequestDto.getEmailId());
				borrower.setMobileNo(loanRequestDto.getMobileNo());
				borrower.setPurposeOfLoan(loanRequestDto.getPurposeOfLoan());
				borrower.setMaritalStatus(loanRequestDto.getMaritalStatus());
				borrower.setRelationshipType(loanRequestDto.getRelationshipType());
				borrower.setTotalSalary(loanRequestDto.getTotalSalary());
				borrower.setRelatedPersonName(loanRequestDto.getRelatedPersonName());
				borrower.setDateOfBirth(loanRequestDto.getDateOfBirth());
				borrower.setNextStep(PROCESS_LOAN_API);
				borrower.setBorrowerUid(borrower1.getBorrowerUid());

				if (borrower.getEmiFrequency().equalsIgnoreCase("Monthly")) {
					LocalDate date = LocalDate.now();
					LocalDate nextDate = date.plusMonths(1);
					borrower.setEmiDate(nextDate.toString());

				}

				borrower.setEmiDate(loanRequestDto.getDateOfEmi().toString());

				borrower.setEmiRate(loanRequestDto.getRateOfInterest());
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
		}

		Borrower borrower = new Borrower();

		borrower.setLoanAggrement(loanRefNo);
		borrower.setUserUniqueId(loanRequestDto.getUserUniqueId());
		borrower.setLenderUid(loanRequestDto.getLenderUid());
		borrower.setLenderName(nbfc.get().getNbfcName());
		borrower.setLenderBrandName(nbfc.get().getBrandName());
		borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
		borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
		borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
		borrower.setSchemeId(product.getSchemeId());
		borrower.setFullName(loanRequestDto.getNameAsPerPan().toUpperCase());
		borrower.setGender(loanRequestDto.getGender());
		borrower.setEmiFrequency(product.getEmiFrequency());
		borrower.setLoanAmount(loanRequestDto.getLoanAmount());
		borrower.setEmiTime(loanRequestDto.getNoOfEMI());
		borrower.setProductId(product.getProductId());
		borrower.setLoanType(product.getLoanType());
		borrower.setEmploymentType(loanRequestDto.getEmploymentType());
		borrower.setEmailId(loanRequestDto.getEmailId());
		borrower.setMobileNo(loanRequestDto.getMobileNo());
		borrower.setPurposeOfLoan(loanRequestDto.getPurposeOfLoan());
		borrower.setMaritalStatus(loanRequestDto.getMaritalStatus());
		borrower.setRelationshipType(loanRequestDto.getRelationshipType());
		borrower.setTotalSalary(loanRequestDto.getTotalSalary());
		borrower.setRelatedPersonName(loanRequestDto.getRelatedPersonName());
		borrower.setDateOfBirth(loanRequestDto.getDateOfBirth());

		if (borrower.getEmiFrequency().equalsIgnoreCase("Monthly")) {
			LocalDate date = LocalDate.now();
			LocalDate nextDate = date.plusMonths(1);
			borrower.setEmiDate(nextDate.toString());

		}

		borrower.setEmiDate(loanRequestDto.getDateOfEmi().toString());

		borrower.setEmiRate(loanRequestDto.getRateOfInterest());
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

	private Borrower checkValidationV2(@Valid LoanRequestDtoV2 loanRequestDto) {

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(loanRequestDto.getLenderUid());

		if (nbfc.isEmpty()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LENDER_ID.getMessage(),
					ResponseCodeEnum.INVALID_LENDER_ID.getCode());

		}

		Product product = productService.getProductByProductIdAndUid(loanRequestDto.getProductId(),
				loanRequestDto.getLenderUid());
		if (product == null) {

			throw new CustomException(ResponseCodeEnum.INVALID_PRODUCT_ID.getMessage(),
					ResponseCodeEnum.INVALID_PRODUCT_ID.getCode());

		}

		if (!product.getApiFlow().equals("Version 2")) {

			throw new CustomException(ResponseCodeEnum.INVALID_VERSION.getMessage(),
					ResponseCodeEnum.INVALID_VERSION.getCode());

		}

		if (loanRequestDto.getLoanAmount() > product.getMaxAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getLoanAmount() < product.getMinAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getNoOfEMI() > Integer.parseInt(product.getMaxTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getNoOfEMI() < Integer.parseInt(product.getMinTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getRateOfInterest() <= product.getMinInterestRate()
				|| loanRequestDto.getRateOfInterest() > product.getMaxInterestRate()) {
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
		borrower.setLenderUid(loanRequestDto.getLenderUid());
		borrower.setLenderName(nbfc.get().getNbfcName());
		borrower.setLenderBrandName(nbfc.get().getBrandName());
		borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
		borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
		borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
		borrower.setSchemeId(product.getSchemeId());
		borrower.setFullName(loanRequestDto.getNameAsPerPan().toUpperCase());
		borrower.setGender(loanRequestDto.getGender());
		borrower.setEmiFrequency(product.getEmiFrequency());
		borrower.setLoanAmount(loanRequestDto.getLoanAmount());
		borrower.setEmiTime(loanRequestDto.getNoOfEMI());
		borrower.setProductId(product.getProductId());
		borrower.setLoanType(product.getLoanType());
		borrower.setEmploymentType(loanRequestDto.getEmploymentType());
		borrower.setEmailId(loanRequestDto.getEmailId());
		borrower.setMobileNo(loanRequestDto.getMobileNo());
		borrower.setPurposeOfLoan(loanRequestDto.getPurposeOfLoan());
		borrower.setMaritalStatus(loanRequestDto.getMaritalStatus());
		borrower.setRelationshipType(loanRequestDto.getRelationshipType());
		borrower.setTotalSalary(loanRequestDto.getTotalSalary());
		borrower.setRelatedPersonName(loanRequestDto.getRelatedPersonName());
		borrower.setDateOfBirth(loanRequestDto.getDateOfBirth());
		borrower.setExtraFields(false);
		if (product.getExtraFields().equals("Active")) {
			borrower.setExtraFields(true);

		}

		if (borrower.getEmiFrequency().equalsIgnoreCase("Monthly")) {
			LocalDate date = LocalDate.now();
			LocalDate nextDate = date.plusMonths(1);
			borrower.setEmiDate(nextDate.toString());

		}

		borrower.setEmiDate(loanRequestDto.getDateOfEmi().toString());

		borrower.setEmiRate(loanRequestDto.getRateOfInterest());
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

	private Borrower checkValidation(@Valid LoanRequestDto loanRequestDto) {

		Optional<Nbfc> nbfc = nbfcService.getNbfcByUid(loanRequestDto.getLenderUid());

		if (nbfc.isEmpty()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LENDER_ID.getMessage(),
					ResponseCodeEnum.INVALID_LENDER_ID.getCode());

		}

		Product product = productService.getProductByProductIdAndUid(loanRequestDto.getProductId(),
				loanRequestDto.getLenderUid());
		if (product == null) {

			throw new CustomException(ResponseCodeEnum.INVALID_PRODUCT_ID.getMessage(),
					ResponseCodeEnum.INVALID_PRODUCT_ID.getCode());

		}
		if (!product.getApiFlow().equals("Version 1")) {

			throw new CustomException(ResponseCodeEnum.INVALID_VERSION.getMessage(),
					ResponseCodeEnum.INVALID_VERSION.getCode());

		}

		if (loanRequestDto.getLoanAmount() > product.getMaxAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getLoanAmount() < product.getMinAmount()) {
			throw new CustomException(ResponseCodeEnum.INVALID_LOAN_AMOUNT.getMessage(),
					ResponseCodeEnum.INVALID_LOAN_AMOUNT.getCode());
		}

		if (loanRequestDto.getNoOfEMI() > Integer.parseInt(product.getMaxTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getNoOfEMI() < Integer.parseInt(product.getMinTenure())) {
			throw new CustomException(ResponseCodeEnum.INVALID_EMI_TIME.getMessage(),
					ResponseCodeEnum.INVALID_EMI_TIME.getCode());
		}

		if (loanRequestDto.getRateOfInterest() < product.getMinInterestRate()
				|| loanRequestDto.getRateOfInterest() > product.getMaxInterestRate()) {
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
		borrower.setLenderUid(loanRequestDto.getLenderUid());
		borrower.setLenderName(nbfc.get().getNbfcName());
		borrower.setLenderBrandName(nbfc.get().getBrandName());
		borrower.setLenderAuthorityEmail(nbfc.get().getAuthorisedPersonEmail());
		borrower.setLenderAuthorityMobileNo(nbfc.get().getAuthorisedPersonMobile());
		borrower.setLenderAuthorityName(nbfc.get().getAuthorisedPersonName());
		borrower.setSchemeId(product.getSchemeId());
		borrower.setFullName(loanRequestDto.getNameAsPerPan().toUpperCase());
		borrower.setGender(loanRequestDto.getGender());
		borrower.setEmiFrequency(product.getEmiFrequency());
		borrower.setLoanAmount(loanRequestDto.getLoanAmount());
		borrower.setEmiTime(loanRequestDto.getNoOfEMI());
		borrower.setProductId(product.getProductId());
		borrower.setLoanType(product.getLoanType());
		borrower.setEmploymentType(loanRequestDto.getEmploymentType());
		borrower.setEmailId(loanRequestDto.getEmailId());
		borrower.setMobileNo(loanRequestDto.getMobileNo());
		borrower.setPurposeOfLoan(loanRequestDto.getPurposeOfLoan());
		borrower.setMaritalStatus(loanRequestDto.getMaritalStatus());
		borrower.setRelationshipType(loanRequestDto.getRelationshipType());
		borrower.setTotalSalary(loanRequestDto.getTotalSalary());
		borrower.setRelatedPersonName(loanRequestDto.getRelatedPersonName());
		borrower.setDateOfBirth(loanRequestDto.getDateOfBirth());

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

		borrower.setEmiRate(loanRequestDto.getRateOfInterest());
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
	public CommonResponse panRequest(PANRequestDto panRequestDto) {

		String sessionToken = panRequestDto.getSessionToken();
		String panNumber = panRequestDto.getPanNumber();

		// Validate session
		LoanResponseDto session = validateSession(sessionToken, PAN_API_STEP);

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new CustomException("Invalid or expired session token", "INVALID_SESSION_TOKEN");
		}
		Borrower borrowerObj = borrower.get();

		Map<String, String> dedupe = dedupeCheck(panNumber, borrowerObj);

		if (dedupe.get("dedupe").equals("Failed")) {
			session.setMessage(dedupe.get("message"));
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		if (dedupe.get("dedupe").equals("Success")) {
			String uid = dedupe.get("message");

			String nextStep = PROCESS_LOAN_API;
			BorrowerPan borrowerPan = borrowerPanRepository.findByBorrowerUid(uid);
			BorrowerAadhaar borrowerAadhar = borrowerAadhaarRepository.findByBorrowerUid(uid);
			BorrowerBank borrowerBankAccount = borrowerBankRepository.findByBorrowerUid(uid);
			BorrowerCibil borrowerCibil = borrowerCibilRepository.findByBorrowerUid(uid);
			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(uid);

			// Set Borrower UID

			PanBorrowerDto panBorrowerDto = new PanBorrowerDto();
			BeanUtils.copyProperties(borrowerPan, panBorrowerDto);
			BorrowerPan borrowerPan2 = new BorrowerPan();
			BeanUtils.copyProperties(panBorrowerDto, borrowerPan2);

			AadharBorrowerDto aadharBorrowerDto = new AadharBorrowerDto();
			BeanUtils.copyProperties(borrowerAadhar, aadharBorrowerDto);
			BorrowerAadhaar borrowerAadhar2 = new BorrowerAadhaar();
			BeanUtils.copyProperties(aadharBorrowerDto, borrowerAadhar2);

			BankBorrowerDto bankBorrowerDto = new BankBorrowerDto();
			BeanUtils.copyProperties(borrowerBankAccount, bankBorrowerDto);
			BorrowerBank borrowerBank2 = new BorrowerBank();
			BeanUtils.copyProperties(bankBorrowerDto, borrowerBank2);

			CibilBorrowerDto cibilBorrowerDto = new CibilBorrowerDto();
			BeanUtils.copyProperties(borrowerCibil, cibilBorrowerDto);
			BorrowerCibil borrowerCibil2 = new BorrowerCibil();
			BeanUtils.copyProperties(cibilBorrowerDto, borrowerCibil2);

			DocBorrowerDto docBorrowerDto = new DocBorrowerDto();
			BeanUtils.copyProperties(borrowerDoc, docBorrowerDto);
			BorrowerDoc borrowerDoc2 = new BorrowerDoc();
			BeanUtils.copyProperties(docBorrowerDto, borrowerDoc2);

			borrowerPan2.setBorrowerUid(sessionToken);
			borrowerAadhar2.setBorrowerUid(sessionToken);
			borrowerBank2.setBorrowerUid(sessionToken);
			borrowerCibil2.setBorrowerUid(sessionToken);
			borrowerDoc2.setBorrowerUid(sessionToken);
			borrowerDoc2.setDigioId(uid);
			borrowerDoc2.setESignRedirectUrl(null);
			borrowerDoc2.setESignStatus(false);

			borrowerPanRepository.save(borrowerPan2);
			borrowerAadhaarRepository.save(borrowerAadhar2);
			borrowerBankRepository.save(borrowerBank2);
			borrowerCibilRepository.save(borrowerCibil2);
			borrowerDocRepository.save(borrowerDoc2);

			session.setMessage("Process to Next Step: ");
			session.setNextStep(nextStep);
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerObj.setNextStep(nextStep);
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);
		}

		String panVerified = apiService.verifyPAN(panNumber, borrower);
		log.info("PAN verified: " + panVerified);

		JSONObject jsonObject = new JSONObject(panVerified);

		if (!jsonObject.optString("code").equals("0000")) {
			session.setMessage("PAN verification failed");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		JSONObject data = jsonObject.optJSONObject("response");

		if (!data.optString("status").equals("valid")) {

			session.setMessage("PAN verification failed -> PAN is not valid");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		if (!data.optBoolean("nameAsPerPanMatch")) {
			session.setMessage("PAN verification failed -> Name does not match");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		if (!data.optBoolean("dateOfBirthMatch")) {
			session.setMessage("PAN verification failed -> Date of Birth does not match");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		if (borrowerObj.isExtraFields()) {
			session.setData(panVerified);
		}

		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrowerObj.getLenderUid());
		configureDto.setProductId(borrowerObj.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(PAN_API_STEP, journeyEngine);
		borrowerObj.setNextStep(nextStep);

		borrowerObj.setResponseMessage("Pan Verified, proceed to the next step");
		borrowerRepository.save(borrowerObj);

		BorrowerPan borrowerPan = new BorrowerPan();

		borrowerPan.setBorrowerUid(sessionToken);
		borrowerPan.setPanNumber(panNumber);
		borrowerPan.setNameInPanNumber(borrowerObj.getFullName());
		borrowerPan.setPanCardFlag(data.optString("status"));

		String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "PAN.json", panVerified);
		borrowerPan.setPanResponse(jsonBody);
		borrowerPanRepository.save(borrowerPan);

		session.setMessage("PAN request accepted, proceed to the next step");
		// Proceed to the next step

		session.setNextStep(nextStep);

		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);

	}

	private Map<String, String> dedupeCheck(String panNumber, Borrower borrowerObj) {
		Map<String, String> dedupe = new HashMap<>();

		List<Borrower> borrowers = borrowerRepository.findByMobileNoAndLoanStatus(borrowerObj.getMobileNo(),
				LoanStatus.LOAN_SUCCESSFUL);
		if (borrowers.size() > 0) {
			BorrowerPan borrowerUids = borrowerPanRepository.findByBorrowerUid(borrowers.get(0).getBorrowerUid());

			if (borrowerUids != null && !borrowerUids.getPanNumber().equals(panNumber)) {
				dedupe.put("dedupe", "Failed");
				dedupe.put("message", "PAN verification failed -> Mobile number Linked with Different PAN");
				return dedupe;
			}
		}

		List<BorrowerPan> borrowerPans = borrowerPanRepository.findByPanNumber(panNumber);
		if (!borrowerPans.isEmpty()) {
			Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerPans.get(0).getBorrowerUid());

			if (borrower.isPresent() && !borrower.get().getMobileNo().equals(borrowerObj.getMobileNo())) {
				dedupe.put("dedupe", "Failed");
				dedupe.put("message", "PAN verification failed -> PAN Linked with Different Mobile number");
				return dedupe;
			}
		}

		if (borrowers.isEmpty()) {
			dedupe.put("dedupe", "passed");
			dedupe.put("message", "Passed");
			return dedupe;
		}

		dedupe.put("dedupe", "Success");
		dedupe.put("message", borrowers.get(0).getBorrowerUid());

		return dedupe;
	}

	private LoanResponseDto validateSession(String sessionToken, String expectedStep) {
		if (!sessionStore.containsKey(sessionToken)) {
			throw new CustomException("Invalid or expired session token", "INVALID_SESSION_TOKEN");
		}

		LoanResponseDto session = sessionStore.get(sessionToken);
		if (!expectedStep.equals(session.getNextStep())) {
			throw new CustomException("Invalid step. Expected: " + expectedStep, "INVALID_STEP");
		}

		if (System.currentTimeMillis() > session.getSessionActiveTime()) {
			sessionStore.remove(sessionToken);
			throw new CustomException("Session expired. Please restart the process.", "SESSION_EXPIRED");
		}

		return session;
	}

	@Override
	public CommonResponse aadharRequest(AadharRequestDto request) {
		String sessionToken = request.getSessionToken();
		String aadharNumber = request.getAadharNumber();

		// Validate session
		LoanResponseDto session = validateSession(sessionToken, AADHAR_API_STEP);

		// Validate AADHAR

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();
		String aadharVerified = apiService.aadharOtpApi(aadharNumber, borrower);
		log.info("Aadhar OTP Shared: " + aadharVerified);

		JSONObject jsonObject = new JSONObject(aadharVerified);

		if (!jsonObject.optString("code").equals("0000")) {
			session.setMessage("Aadhar OTP Not Shared ");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		JSONObject data = jsonObject.optJSONObject("response");

		if (!data.optString("message").equalsIgnoreCase("OTP Sent.")) {

			session.setMessage("Aadhar OTP Not Shared -> Aadhar is not valid");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		if (borrowerObj.isExtraFields()) {
			session.setData(aadharVerified);
		}
		JSONObject aadharData = data.optJSONObject("data");
		String requestId = aadharData.optString("requestId");
		adharOtp.put(sessionToken, requestId);

		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrowerObj.getLenderUid());
		configureDto.setProductId(borrowerObj.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(AADHAR_API_STEP, journeyEngine);
		borrowerObj.setNextStep(nextStep);
		session.setMessage(
				"Your Aadhaar OTP has been sent to your registered mobile number. Please check and verify, proceed to the next step");

		borrowerObj.setResponseMessage(session.getMessage());
		borrowerRepository.save(borrowerObj);
		BorrowerAadhaar borrowerAadhaar = new BorrowerAadhaar();
		borrowerAadhaar.setBorrowerUid(sessionToken);
		borrowerAadhaar.setAadharNumber(aadharNumber);
		borrowerAadhaarRepository.save(borrowerAadhaar);

//		Object object = new

		// Proceed to the next step

		session.setNextStep(nextStep);

		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);
	}

	@Override
	public CommonResponse aadharOtpRequest(@Valid AadharOtpRequestDto aadharOtpRequestDto) {
		String sessionToken = aadharOtpRequestDto.getSessionToken();
		String aadharOtp = aadharOtpRequestDto.getAadharOtp();

		// Validate session
		LoanResponseDto session = validateSession(sessionToken, AADHAR_OTP_API_STEP);

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();
		String aadharVerified = apiService.verifyAadhar(aadharOtp, borrower, adharOtp.get(sessionToken));

		JSONObject jsonObject = new JSONObject(aadharVerified);

		if (!jsonObject.optString("code").equals("0000")) {
			session.setMessage("Aadhar Not Verified");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		JSONObject data = jsonObject.optJSONObject("response");

		if (!data.optString("status_code").equalsIgnoreCase("200")) {

			session.setMessage("Aadhar Not Verified");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		JSONObject aadharData = data.optJSONObject("data");
		String name = aadharData.optString("full_name");

		if (!NameMatch.isNameMatching(borrowerObj.getFullName(), name, 60)) {

			session.setMessage("Aadhar verification failed -> Name does not match with Pan -- " + name + "");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrowerObj.getLenderUid());
		configureDto.setProductId(borrowerObj.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(AADHAR_OTP_API_STEP, journeyEngine);
		borrowerObj.setNextStep(nextStep);
		session.setMessage("Your Aadhaar Verified. Please check and verify, proceed to the next step");

		borrowerObj.setResponseMessage(session.getMessage());
		borrowerRepository.save(borrowerObj);
		updateBorrowerAadhar(borrowerObj, aadharVerified);
		session.setNextStep(nextStep);
		if (borrowerObj.isExtraFields()) {
			session.setData(aadharVerified);
		}

		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);
	}

	private void updateBorrowerAadhar(Borrower borrowerObj, String aadharVerified) {

		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(borrowerObj.getBorrowerUid());
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

	}

	@Override
	public CommonResponse bankAccountRequest(@Valid BankAccountRequestDto bankAccountRequestDto) {
		String sessionToken = bankAccountRequestDto.getSessionToken();
		String bankAccount = bankAccountRequestDto.getBankAccount();

		// Validate session
		LoanResponseDto session = validateSession(sessionToken, PENNY_DROP_API_STEP);
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("ifsc", bankAccountRequestDto.getIfscCode());
		jsonObject.put("bankAccount", bankAccount);
		jsonObject.put("name", borrower.get().getFullName());
		jsonObject.put("phone", borrower.get().getMobileNo());
		jsonObject.put("apiSource", "D1");

		String bankVerified = apiService.verifyBankAccount(jsonObject);

		if (borrowerObj.isExtraFields()) {
			session.setData(bankVerified);
		}

		JSONObject jsonObject1 = new JSONObject(bankVerified);

		if (!jsonObject1.optString("code").equals("SUCCESS")) {
			session.setMessage("Bank Not Verified");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		JSONObject data = jsonObject1.optJSONObject("response");

		if (!data.optBoolean("verified")) {

			session.setMessage("Bank Not Verified");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}
		String name = data.optString("nameAtBank");

		if (!NameMatch.isNameMatching(borrowerObj.getFullName(), name, 60)) {

			session.setMessage("Bank verification failed -> Name does not match with Pan -- " + name + "");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);

			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		ConfigureDto configureDto = new ConfigureDto();
		configureDto.setUid(borrowerObj.getLenderUid());
		configureDto.setProductId(borrowerObj.getProductId());
		// Start the journey
		JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
		String nextStep = provideNextStep(PENNY_DROP_API_STEP, journeyEngine);
		borrowerObj.setNextStep(nextStep);
		session.setMessage("Your Bank Account Verified, proceed to the next step");

		borrowerObj.setResponseMessage(session.getMessage());
		borrowerRepository.save(borrowerObj);
		BorrowerBank borrowerBank = new BorrowerBank();
		borrowerBank.setBorrowerUid(sessionToken);
		borrowerBank.setBankAccountNo(bankAccount);
		borrowerBank.setIfscCode(bankAccountRequestDto.getIfscCode());
		String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "BANK.json", bankVerified);
		borrowerBank.setBankResponse(jsonBody);
		borrowerBank.setAccountType(bankAccountRequestDto.getAccountType());
		borrowerBankRepository.save(borrowerBank);

		session.setNextStep(nextStep);

		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);
	}

	@Override
	public CommonResponse creditScoreRequest(@Valid CibilRequestDto cibilRequestDto) {
		String sessionToken = cibilRequestDto.getSessionToken();
		Boolean userConsent = cibilRequestDto.getConsent();

		// Validate session
		LoanResponseDto session = validateSession(sessionToken, CIBIL_API_STEP);

		if (!userConsent) {
			session.setMessage("Credit Score request rejected, please provide consent");
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);

		}
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();

		String creditScore = apiService.getCreditScore(borrowerObj);

		JSONObject data = ValidatedRequest.validateCreditScore(creditScore);

		if (borrowerObj.isExtraFields()) {
			session.setData(creditScore);
		}

		if (data.has("code") && data.optString("code").equalsIgnoreCase("008")) {

			BorrowerCibil borrowerCibil = new BorrowerCibil();
			borrowerCibil.setBorrowerUid(sessionToken);
			borrowerCibil.setNtc(true);
			String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "CIBIL.json", creditScore);
			borrowerCibil.setCibilResponse(jsonBody);
			borrowerCibilRepository.save(borrowerCibil);

			session.setMessage("Cibil score request rejected -> Borrower is NTC");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);

		} else {

			BorrowerCibil borrowerCibil = new BorrowerCibil();
			borrowerCibil.setBorrowerUid(sessionToken);
			borrowerCibil.setNtc(false);
			borrowerCibil.setCibilScore(data.optString("SCORE"));
			borrowerCibil.setCibilScore(data.optString("SCORE"));
			borrowerCibil.setActiveCredit(data.optString("CreditAccountActive"));
			borrowerCibil.setSuitFile(data.optString("CreditAccountDefault"));
			borrowerCibil.setSettledLoan(data.optString("CreditAccountClosed"));
			borrowerCibil.setWriteOff(data.optString("CADSuitFiledCurrentBalance"));
			borrowerCibil.setFirstYearDPD(data.optString("FIRST_YEAR"));
			borrowerCibil.setSecondYearDPD(data.optString("SECOND_YEAR"));
			borrowerCibil.setCaisAccountDetails(data.optString("ACCOUNT_DETAILS"));
			borrowerCibil.setDpd(data.optString("DAYS_PAST_DUE"));
			String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "CIBIL.json", creditScore);
			borrowerCibil.setCibilResponse(jsonBody);
			borrowerCibilRepository.save(borrowerCibil);

			ConfigureDto configureDto = new ConfigureDto();
			configureDto.setUid(borrowerObj.getLenderUid());
			configureDto.setProductId(borrowerObj.getProductId());
			// Start the journey
			JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
			String nextStep = provideNextStep(CIBIL_API_STEP, journeyEngine);
			borrowerObj.setNextStep(nextStep);

			session.setMessage("Credit Score request accepted, proceed to the next step");

			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			session.setNextStep(nextStep);

			sessionStore.put(sessionToken, session);

			return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);

		}

	}

	@Override
	public CommonResponse uploadUserImage(MultipartFile file, String sessionToken) {

		LoanResponseDto session = validateSession(sessionToken, USER_IMAGE_API_STEP);

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();

		if (file.isEmpty()) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"No file selected for upload.", session);
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.matches("image/(png|jpeg|jpg)")) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"Invalid file type. Only PNG, JPG, and JPEG are allowed.", session);
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
			ConfigureDto configureDto = new ConfigureDto();
			configureDto.setUid(borrowerObj.getLenderUid());
			configureDto.setProductId(borrowerObj.getProductId());
			// Start the journey
			JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
			String nextStep = provideNextStep(USER_IMAGE_API_STEP, journeyEngine);
			borrowerObj.setNextStep(nextStep);
			session.setMessage("User image uploaded successfully.");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			session.setNextStep(nextStep);

			session.setData(null);
			sessionStore.put(sessionToken, session);

			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(sessionToken);

			if (borrowerDoc == null) {
				borrowerDoc = new BorrowerDoc();
				borrowerDoc.setBorrowerUid(sessionToken);
				String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "userImage.json", base64Image);
				borrowerDoc.setUserImage(jsonBody);
				borrowerDocRepository.save(borrowerDoc);
			}
			borrowerDoc.setBorrowerUid(sessionToken);
			String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "userImage.json", base64Image);
			borrowerDoc.setUserImage(jsonBody);
			borrowerDocRepository.save(borrowerDoc);

			return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					"Image uploaded successfully.", session);
		} catch (Exception e) {
			return new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(), "Failed to process the image file.", session);
		}
	}

	@Override
	public CommonResponse uploadPanImage(MultipartFile file, String sessionToken) {
		LoanResponseDto session = validateSession(sessionToken, PAN_IMAGE_API_STEP);

		if (file.isEmpty()) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"No file selected for upload.", session);
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.matches("image/(png|jpeg|jpg)")) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"Invalid file type. Only PNG, JPG, and JPEG are allowed.", session);
		}
		// Validate file size (max size 2MB)
		if (file.getSize() > MAX_PDF_SIZE) { // 2MB
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"File size exceeds 2MB limit.", session);
		}

		// Process the image file (e.g., save to disk or database)
		// Save the file (you can implement your file saving logic here)

		// After uploading the image, update the session to indicate that the image
		// upload step is done
		session.setMessage("PAN image uploaded successfully.");
		session.setNextStep(AADHAR_IMAGE_API_STEP); // Update this step accordingly to the flow
		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				"Image uploaded successfully.", session);
	}

	@Override
	public CommonResponse uploadAadharImage(MultipartFile file, String sessionToken) {
		LoanResponseDto session = validateSession(sessionToken, AADHAR_IMAGE_API_STEP);

		if (file.isEmpty()) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"No file selected for upload.", session);
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.matches("image/(png|jpeg|jpg)")) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"Invalid file type. Only PNG, JPG, and JPEG are allowed.", session);
		}
		// Validate file size (max size 2MB)
		if (file.getSize() > MAX_PDF_SIZE) { // 2MB
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"File size exceeds 2MB limit.", session);
		}

		// Process the image file (e.g., save to disk or database)
		// Save the file (you can implement your file saving logic here)

		// After uploading the image, update the session to indicate that the image
		// upload step is done
		session.setMessage("Aadhar image uploaded successfully.");
		session.setNextStep(BANK_DOC_API_STEP); // Update this step accordingly to the flow
		sessionStore.put(sessionToken, session);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				"Image uploaded successfully.", session);
	}

	@Override
	public CommonResponse uploadbankStatementPdf(MultipartFile file, String sessionToken) {
		LoanResponseDto session = validateSession(sessionToken, BANK_DOC_API_STEP);

		if (file.isEmpty()) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"No file selected for upload.", session);
		}

		// Validate file size (max size 2MB)
		if (file.getSize() > MAX_PDF_SIZE) { // 2MB
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"File size exceeds 2MB limit.", session);
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.equals("application/pdf")) {
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(),
					"Invalid file type. Only PDF files are allowed.", session);
		}
		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();
		try {
			byte[] fileBytes = file.getBytes();
			String base64Image = Base64.getEncoder().encodeToString(fileBytes);

			// Optionally save the Base64 string to the database or log it

			// After uploading the image, update the session to indicate that the image
			// upload step is done
			ConfigureDto configureDto = new ConfigureDto();
			configureDto.setUid(borrowerObj.getLenderUid());
			configureDto.setProductId(borrowerObj.getProductId());
			// Start the journey
			JourneyEngine journeyEngine = journeyEngineService.findJourneyEngine(configureDto);
			String nextStep = provideNextStep(BANK_DOC_API_STEP, journeyEngine);
			borrowerObj.setNextStep(nextStep);
			session.setMessage("Bank statement uploaded successfully.");
			borrowerObj.setResponseMessage(session.getMessage());
			borrowerRepository.save(borrowerObj);
			session.setNextStep(nextStep);
			sessionStore.put(sessionToken, session);

			BorrowerDoc borrowerDoc = borrowerDocRepository.findByBorrowerUid(sessionToken);

			if (borrowerDoc == null) {
				borrowerDoc = new BorrowerDoc();
				borrowerDoc.setBorrowerUid(sessionToken);
				String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "bankPdf.json", base64Image);
				borrowerDoc.setBankStatementPdf(jsonBody);
				borrowerDocRepository.save(borrowerDoc);
			}
			borrowerDoc.setBorrowerUid(sessionToken);
			String jsonBody = awsS3Service.uploadJsonToS3(sessionToken + "bankPdf.json", base64Image);
			borrowerDoc.setBankStatementPdf(jsonBody);
			borrowerDocRepository.save(borrowerDoc);

			return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
					"Bank statement uploaded successfully.", session);
		} catch (Exception e) {
			return new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					ResponseCodeEnum.FILE_UPLOAD_FAILED.getCode(), "Failed to process the image file.", session);
		}
	}

	@Override
	public CommonResponse processLoanRequest(@Valid ExtraInfoDto extraInfoDto) {
		String sessionToken = extraInfoDto.getSessionToken();
		Boolean consentForLoan = extraInfoDto.getConsentForLoan();
		LoanResponseDto session = validateSession(sessionToken, PROCESS_LOAN_API);

		if (!consentForLoan) {
			session.setMessage("Loan request rejected, please provide consent");
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);

		}

		if (extraInfoDto.getWebhookUrl() != null && !extraInfoDto.getWebhookUrl().contains("https://")) {
			session.setMessage("Loan request rejected, Invalid webhook url");
			return new CommonResponse(HttpStatus.BAD_REQUEST.value(), ResponseCodeEnum.INVALID_REQUEST.getCode(),
					ResponseCodeEnum.INVALID_REQUEST.getMessage(), session);
		}

		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(sessionToken);

		if (borrower.isEmpty()) {
			throw new RuntimeException("Invalid or expired session token");
		}
		Borrower borrowerObj = borrower.get();

		borrowerObj.setSecondaryAddressLine1(extraInfoDto.getSecondaryAddressLine1());
		borrowerObj.setSecondaryArea(extraInfoDto.getSecondaryArea());
		borrowerObj.setSecondaryCity(extraInfoDto.getSecondaryCity());
		borrowerObj.setSecondaryState(extraInfoDto.getSecondaryState());
		borrowerObj.setSecondaryPinCode(extraInfoDto.getSecondaryPinCode());
		borrowerObj.setSecondaryLandmark(extraInfoDto.getSecondaryLandmark());
		borrowerObj.setConsentForLoan(consentForLoan);
		if (extraInfoDto.getUdf1() != null) {
			borrowerObj.setUdf1(extraInfoDto.getUdf1());

		}
		if (extraInfoDto.getUdf2() != null) {
			borrowerObj.setUdf2(extraInfoDto.getUdf2());
		}
		if (extraInfoDto.getUdf3() != null) {
			borrowerObj.setUdf3(extraInfoDto.getUdf3());
		}
		if (extraInfoDto.getUdf4() != null) {
			borrowerObj.setUdf4(extraInfoDto.getUdf4());
		}
		if (extraInfoDto.getUdf5() != null) {
			borrowerObj.setUdf5(extraInfoDto.getUdf5());
		}
		if (extraInfoDto.getWebhookUrl() != null) {
			borrowerObj.setWebhookUrl(extraInfoDto.getWebhookUrl());
		}
		session.setMessage("Loan request processed successfully. Thank you.");

		session.setNextStep(STEP_COMPLETE);
		borrowerObj.setResponseMessage(session.getMessage());
		borrowerObj.setNextStep(session.getNextStep());

		borrowerRepository.save(borrowerObj);

		// Calling API FOR ALL CLOUD FUNCTIONS
		Map<String, String> response = allCloudApiService.callAllCloudApi(borrowerObj);
		session.setMessage(response.get("message"));

		session.setNextStep(STEP_COMPLETE);
		borrowerObj.setResponseMessage(session.getMessage());
		borrowerObj.setNextStep(session.getNextStep());
		borrowerRepository.save(borrowerObj);
		sessionStore.put(sessionToken, session);
		sessionStore.remove(sessionToken);

		return new CommonResponse(HttpStatus.OK.value(), ResponseCodeEnum.REQUEST_PROCESSED.getCode(),
				ResponseCodeEnum.REQUEST_PROCESSED.getMessage(), session);
	}

	private String provideNextStep(String currentStep, JourneyEngine jEngine) {
		String nextStep = null;
		int currentStepCount = 0;

		// Create a map with step counts as keys and step names as values
		Map<Integer, String> stepMap = new LinkedHashMap<>();
		stepMap.put(jEngine.getPan(), "PAN_API_STEP");
		stepMap.put(jEngine.getAadhaar(), "AADHAR_API_STEP");
		stepMap.put(jEngine.getAadhaarOtp(), "AADHAR_OTP_API_STEP");
		stepMap.put(jEngine.getCibil(), "CIBIL_API_STEP");
		stepMap.put(jEngine.getPenny(), "PENNY_DROP_API_STEP");
		stepMap.put(jEngine.getBankStatement(), "BANK_DOC_API_STEP");
		stepMap.put(jEngine.getPanImage(), "PAN_IMAGE_API_STEP");
		stepMap.put(jEngine.getAadhaarImage(), "AADHAR_IMAGE_API_STEP");
		stepMap.put(jEngine.getProcessLoan(), "PROCESS_LOAN_API");
		stepMap.put(jEngine.getUserImage(), "USER_IMAGE_API_STEP");

		// Get the current step count
		switch (currentStep) {
		case "PAN_API_STEP":
			currentStepCount = jEngine.getPan();
			break;
		case "AADHAR_API_STEP":
			currentStepCount = jEngine.getAadhaar();
			break;
		case "AADHAR_OTP_API_STEP":
			currentStepCount = jEngine.getAadhaarOtp();
			break;
		case "CIBIL_API_STEP":
			currentStepCount = jEngine.getCibil();
			break;
		case "PENNY_DROP_API_STEP":
			currentStepCount = jEngine.getPenny();
			break;
		case "BANK_DOC_API_STEP":
			currentStepCount = jEngine.getBankStatement();
			break;
		case "PAN_IMAGE_API_STEP":
			currentStepCount = jEngine.getPanImage();
			break;
		case "AADHAR_IMAGE_API_STEP":
			currentStepCount = jEngine.getAadhaarImage();
			break;
		case "USER_IMAGE_API_STEP":
			currentStepCount = jEngine.getUserImage();
			break;
		case "PROCESS_LOAN_API":
			currentStepCount = jEngine.getProcessLoan();
			break;
		}

		// Find the next step based on the current step count
		for (Map.Entry<Integer, String> entry : stepMap.entrySet()) {
			if (entry.getKey() == currentStepCount + 1) {
				nextStep = entry.getValue();
				break;
			}
		}

		return nextStep;
	}

//	@Override
//	public String generateHtmlContent(String borrowerUid) {
//		StringBuilder htmlBuilder = new StringBuilder();
//
//		Optional<Borrower> borrower = borrowerRepository.findByBorrowerUid(borrowerUid);
//		BorrowerAadhaar borrowerAadhaar = borrowerAadhaarRepository.findByBorrowerUid(borrowerUid);
//		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//
//		htmlBuilder.append("<!DOCTYPE html>").append("<html>").append("<head>").append("<title>LOAN AGREEMENT</title>")
//				.append("<style>").append("body { font-family: sans-serif; }")
//				.append(".page { page-break-after: always; }") // Ensures content breaks to a new page
//				.append(".table-section { page-break-before: always; }") // Ensures the table starts on a new page
//				.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }")
//				.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
//				.append("th { background-color: #f4f4f4; }").append("</style>").append("</head>").append("<body>")
//				.append("<div class='page'>")
//				.append("<img class='alignnone  wp-image-1016' src='https://hindon.co/wp-content/uploads/2021/07/logo-new-1-300x166.png' width='141' height='78' alt='hindon logo'></img>")
//				.append("<h1 style='text-align: center;'>DECLARATION AND UNDERTAKING</h1>").append("<p>I ")
//				.append(borrower.get().getFullName()).append(" ").append(borrowerAadhaar.getCareOfInAadhar())
//				.append(" the undersigned herein, do hereby state, declare, and solemnly affirm as follows:</p>")
//				.append("<p>1. I have carefully reviewed and understood the contents of all the documents related to the credit facilities/loan availed by me from Hindon Mercantile Limited (CIN: U34300DL1985PLC021785).</p>")
//				.append("<p>2. The terms and conditions outlined in the facility documents, along with any ancillary agreements, have been explained to me in a language that I fully understand.</p>")
//				.append("<p>3. I confirm that I have comprehended all the contents of the said documents and that I have signed them of my own free will, with full knowledge and acceptance of the terms and conditions contained therein.</p>")
//				.append("<p>4. By signing these documents, I acknowledge that I am fully aware of and agree to abide by all the terms and conditions stipulated in the facility documents.</p>")
//				.append("<p>I declare that the statements made hereinabove are true and correct to the best of my knowledge and belief.</p>")
//				.append("<p><strong>Name of Borrower : </strong>").append(borrower.get().getFullName()).append("</p>")
//				.append("<p><strong>Signature of Borrower : </strong></p>").append("<p><strong>Date : </strong>")
//				.append(today).append("</p>").append("<p><strong>Place : </strong></p>").append("</div>")
//				.append("<div class='page'>") // Start table section on a new page
//				.append("<img class='alignnone  wp-image-1016' src='https://hindon.co/wp-content/uploads/2021/07/logo-new-1-300x166.png' width='141' height='78' alt='hindon logo'></img>")
//				.append("<h1 style='text-align: center;'>SANCTION LETTER</h1>")
//				.append("<p> <strong style='text-align: left;'>Hindon Mercantile Limited </strong><strong style='float: right;'>Date : ")
//				.append(today).append("</strong></p>").append("<p>201, 2nd Floor, Best Sky Tower,</p>")
//				.append("<p>Netaji Subhash Place, Delhi-110034 </p>").append("<p><strong>Dear ")
//				.append(borrower.get().getFullName()).append(" : </strong></p>")
//				.append("<p>This loan sanction letter is made in reference to your loan application number ")
//				.append(borrower.get().getLoanAggrement()).append(" Date : ").append(today)
//				.append(" Based on the information you provided in your loan application; we are pleased to inform you of the approval of your loan based on the following terms and conditions:</p>")
//
//				.append("<table>").append("<thead>").append("<tr>").append("<th>Sr. No.</th>")
//				.append("<th>Particulars</th>").append("<th>Details</th>").append("</tr>").append("</thead>")
//				.append("<tbody>").append("<tr>").append("<td>1</td>").append("<td>Loan Sanctioned Amount</td>")
//				.append("<td>").append(borrower.get().getSectionAmount()).append("</td>").append("</tr>").append("<tr>")
//				.append("<td>2</td>").append("<td>Loan Date</td>").append("<td>").append(today).append("</td>")
//				.append("</tr>").append("<tr>").append("<td>3</td>").append("<td>Rate of Interest</td>").append("<td>")
//				.append(borrower.get().getEmiRate()).append("</td>").append("</tr>").append("<tr>").append("<td>4</td>")
//				.append("<td>Loan Tenure</td>").append("<td>").append(borrower.get().getEmiTime()).append("</td>")
//				.append("</tr>").append("<tr>").append("<td>5</td>").append("<td>Processing Fees + GST</td>")
//				.append("<td>").append("").append("</td>").append("</tr>").append("<tr>").append("<td>6</td>")
//				.append("<td>Instalment Amount</td>").append("<td>").append("").append("</td>").append("</tr>")
//				.append("<tr>").append("<td>7</td>").append("<td>Number of Instalments</td>").append("<td>")
//				.append(borrower.get().getEmiTime()).append("</td>").append("</tr>").append("<tr>").append("<td>8</td>")
//				.append("<td>Instalment Due Date</td>").append("<td>").append(borrower.get().getEmiDate())
//				.append("</td>").append("</tr>").append("<tr>").append("<td>9</td>")
//				.append("<td>Instalment Frequency</td>").append("<td>").append(borrower.get().getEmiFrequency())
//				.append("</td>").append("</tr>").append("<tr>").append("<td>10</td>").append("<td>Penal Charges</td>")
//				.append("<td>").append("").append("</td>").append("</tr>").append("<tr>").append("<td>11</td>")
//				.append("<td>Penal Charges</td>").append("<td>").append("").append("</td>").append("</tr>")
//				.append("<tr>").append("<td>12</td>").append("<td>Bounce Charges</td>").append("<td>").append("")
//				.append("</td>").append("</tr>").append("<tr>").append("<td>13</td>")
//				.append("<td>Annual Percentage Rate (APR)</td>").append("<td>").append("").append("</td>")
//				.append("</tr>").append("</tbody>").append("</table>")
//				.append("<p><strong>Terms and Conditions</strong></p>").append("<ul>")
//				.append("<li>Loan Disbursement will be made in the designated bank account as provided in the Loan Agreement.</li>")
//				.append("<li>Processing fees (including GST) will be deducted from the Loan Amount before disbursal.</li>")
//				.append("<li>This sanction can be revoked and/or cancelled on the sole discretion of the Company.</li>")
//				.append("<li>The repayment shall be in accordance with the repayment schedule as enclosed with the Loan Agreement.</li>")
//				.append("<li>You understand and acknowledge that the language of this Sanction letter is known to you and that you have read and understood in vernacular language, the features of the loan product and the terms and conditions mentioned herein and contained in any other loan documents and shall abide by them including any amendment thereto, with free will and volition.</li>")
//				.append("</ul>").append("<p>Regards,</p>").append("<p>Hindon Mercantile Limited</p>")
//				.append("<p>Designation: CFO</p>").append("</div>").append("<div class='page'>")
//				.append("<img class='alignnone  wp-image-1016' src='https://hindon.co/wp-content/uploads/2021/07/logo-new-1-300x166.png' width='141' height='78' alt='hindon logo'></img>")
//				.append("<h1 style='text-align: center;'>LOAN FACILITY AGREEMENT</h1>")
//				.append("<p>THIS LOAN FACILITY AGREEMENT (“Agreement”) is made at New Delhi, on ").append(today)
//				.append("(“Execution Date”)</p>").append("<p><strong>BY AND AMONG</strong></p>")
//				.append("<p><strong>Hindon Mercantile Limited,</strong> hereinafter referred to <strong>“Lender”</strong> (details described in <strong>Schedule 1</strong>), (which expression shall, unless it be repugnant to the subject or context thereof, be deemed to mean and include its successors and permitted assignees) of the FIRST PART;</p>")
//				.append("<p><strong>AND</strong></p>").append("<p><strong>").append(borrower.get().getFullName())
//				.append("</strong>, hereinafter referred to <strong>“Borrower”</strong> (details described in <strong>Schedule 1</strong>), (which expression shall, unless it be repugnant to the subject or context thereof, be deemed to mean include its successors and permitted assigns) of the SECOND PART;</p>")
//				.append("</div>").append("</body>").append("</html>");
//
//		return htmlBuilder.toString();
//	}

	@Override
	public CommonResponse esign(@Valid EsignDto esignDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
