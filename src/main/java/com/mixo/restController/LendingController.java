package com.mixo.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mixo.dto.AadharVerificationOtpRequestDto;
import com.mixo.dto.AadharVerificationRequestDto;
import com.mixo.dto.BankAccountVerificationRequestDto;
import com.mixo.dto.CustomerUidDto;
import com.mixo.dto.EsignDownloadRequestDto;
import com.mixo.dto.EsignVDto;
import com.mixo.dto.FullKycDto;
import com.mixo.dto.LendingRequestDtoV1;
import com.mixo.dto.LendingRequestDtoV2;
import com.mixo.dto.LoanStatusRequestDto;
import com.mixo.dto.PanVerificationRequest;
import com.mixo.service.LandingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/lending")
@RequiredArgsConstructor
@Slf4j
public class LendingController {

	@Autowired
	LandingService landingService;

	@PostMapping("/createCustomer")
	public ResponseEntity<?> createCustomer(@Valid @RequestBody PanVerificationRequest request) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: ISO-8601 formatting
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(request);
//			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received createCustomer:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging createCustomer", e);
		}
		return ResponseEntity.ok(landingService.verifyPan(request));
	}

	@PostMapping("/getCustomerUid")
	public ResponseEntity<?> getCustomerUid(@Valid @RequestBody CustomerUidDto request) {
		return ResponseEntity.ok(landingService.getCustomerUid(request));
	}

	@PostMapping("/getLoanStatus")
	public ResponseEntity<?> getLoanStatus(@Valid @RequestBody LoanStatusRequestDto request) {
		return ResponseEntity.ok(landingService.getLoanStatus(request));
	}

	@PostMapping("/aadhar")
	public ResponseEntity<?> processAadhar(@Valid @RequestBody AadharVerificationRequestDto request) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received aadhar:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging aadhar", e);
		}
		return ResponseEntity.ok(landingService.aadharRequest(request));
	}
	
	@PostMapping("v2/aadhar")
	public ResponseEntity<?> processAadharV2(@Valid @RequestBody AadharVerificationRequestDto request) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received aadhar:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging aadhar", e);
		}
		return ResponseEntity.ok(landingService.aadharRequestv2(request));
	}
	
	@PostMapping("v2/aadharStatus")
	public ResponseEntity<?> aadharStatus(@Valid @RequestBody AadharVerificationRequestDto request) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received aadhar:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging aadhar", e);
		}
		return ResponseEntity.ok(landingService.aadharStatusRequestv2(request));
	}

	@PostMapping("/aadharOtp")
	public ResponseEntity<?> processAadharOtp(@Valid @RequestBody AadharVerificationOtpRequestDto request) {

		return ResponseEntity.ok(landingService.aadharOtpRequest(request));
	}

	@PostMapping("/bankAccount")
	public ResponseEntity<?> processbankAccount(
			@Valid @RequestBody BankAccountVerificationRequestDto bankAccountRequestDto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(bankAccountRequestDto);
			log.info("Received bankAccountRequestDto:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging bankAccountRequestDto", e);
		}
		return ResponseEntity.ok(landingService.bankVerification(bankAccountRequestDto));
	}

	@PostMapping("/userImage")
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("uid") String uid) {

		return ResponseEntity.ok(landingService.uploadUserImage(file, uid));

	}

	@PostMapping("/v1/createLoan")
	public ResponseEntity<?> createLoanV1(@Valid @RequestBody LendingRequestDtoV1 lendingRequestDtoV1) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: ISO-8601 formatting
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(lendingRequestDtoV1);
//			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received lendingRequestDtoV1:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging lendingRequestDtoV1", e);
		}
		return ResponseEntity.ok(landingService.lendingRequestV1(lendingRequestDtoV1));
	}

	@PostMapping("/v2/createLoan")
	public ResponseEntity<?> createLoanV2(@Valid @RequestBody LendingRequestDtoV2 lendingRequestDtoV2) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: ISO-8601 formatting
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(lendingRequestDtoV2);
//			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received LendingRequestDtoV2:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging LendingRequestDtoV2", e);
		}
		return ResponseEntity.ok(landingService.lendingRequestV2(lendingRequestDtoV2));
	}

//	@PostMapping("/v3/createLoan")
//	public ResponseEntity<?> createLoanV3(@Valid @RequestBody BankAccountVerificationRequestDto bankAccountRequestDto) {
//		try {
//	        ObjectMapper mapper = new ObjectMapper();
//	        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
//	        String prettyJson = mapper.writeValueAsString(request);
//	        log.info("Received aadhar:\n{}", prettyJson);
//	    } catch (Exception e) {
//	        log.error("Error while logging aadhar", e);
//	    }
//		return ResponseEntity.ok(landingService.bankVerification(bankAccountRequestDto));
//	}

	@PostMapping("esign")
	public ResponseEntity<?> esign(@Valid @RequestBody EsignVDto esignVDto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(esignVDto);
			log.info("Received esign:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging esign", e);
		}
		return ResponseEntity.ok(landingService.esign(esignVDto));
	}
	
	@PostMapping("v2/esign")
	public ResponseEntity<?> esignv2(@Valid @RequestBody EsignVDto esignVDto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(esignVDto);
			log.info("Received esign:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging esign", e);
		}
		return ResponseEntity.ok(landingService.esign(esignVDto));
	}

	@PostMapping("enach")
	public ResponseEntity<?> enach(@Valid @RequestBody EsignVDto esignVDto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(esignVDto);
			log.info("Received enach:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging enach", e);
		}
		return ResponseEntity.ok(landingService.enach(esignVDto));
	}

	@GetMapping("/next-step/{uid}")
	public ResponseEntity<?> getNextStep(@PathVariable String uid) {
		return ResponseEntity.ok(landingService.getNextStep(uid));
	}

	@PostMapping("/fullKyc")
	public ResponseEntity<?> fullKyc(@Valid @RequestBody FullKycDto fullKycDto) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: ISO-8601 formatting
			mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
			String prettyJson = mapper.writeValueAsString(fullKycDto);
//			String prettyJson = mapper.writeValueAsString(request);
			log.info("Received fullKycDto:\n{}", prettyJson);
		} catch (Exception e) {
			log.error("Error while logging fullKycDto", e);
		}
		return ResponseEntity.ok(landingService.fullKyc(fullKycDto));
	}
	
	
	@PostMapping("/esignedPdf")
	public ResponseEntity<?> esignedPdf(@RequestParam("file") MultipartFile file, @RequestParam("borrowerId") String borrowerId) {

		return ResponseEntity.ok(landingService.uploadEsignedPdf(file, borrowerId));

	}
	
	@PostMapping("/logPdf")
	public ResponseEntity<?> logPdf(@RequestParam("file") MultipartFile file, @RequestParam("borrowerId") String borrowerId) {

		return ResponseEntity.ok(landingService.uploadLogPdf(file, borrowerId));

	}

	@PostMapping("/getEsign")
	public ResponseEntity<?> getEsign(@Valid @RequestBody EsignDownloadRequestDto request) {
		byte[] pdfBytes = landingService.getEsign(request);

		if (pdfBytes == null || pdfBytes.length == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PDF not found for the given request");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.builder("inline").filename("eSignDocument.pdf").build());

		return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}

}
