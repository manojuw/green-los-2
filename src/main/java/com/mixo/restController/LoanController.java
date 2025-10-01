package com.mixo.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import com.mixo.config.CustomException;
import com.mixo.config.HtmlToPdfService;
import com.mixo.dto.AadharOtpRequestDto;
import com.mixo.dto.AadharRequestDto;
import com.mixo.dto.BankAccountRequestDto;
import com.mixo.dto.CibilRequestDto;
import com.mixo.dto.EsignDto;
import com.mixo.dto.ExtraInfoDto;
import com.mixo.dto.LoanRequestDto;
import com.mixo.dto.LoanRequestDtoV2;
import com.mixo.dto.LoanRequestDtoV3;
import com.mixo.dto.PANRequestDto;
import com.mixo.service.LoanService;
import com.mixo.service.PdfService;
import com.mixo.test.KycService;
import com.mixo.utils.CommonResponse;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/loans")
public class LoanController {

	@Autowired
	LoanService loanService;

	@Autowired
	PdfService pdfService;

	@Autowired
	KycService kycService;

	private RestTemplate restTemplate = new RestTemplate();;

	@Autowired
	private HtmlToPdfService spdfService;

	@Autowired
	private TemplateEngine templateEngine;

	@PostMapping("v1/request") // V1 API
	public ResponseEntity<CommonResponse> createLoan(@Valid @RequestBody LoanRequestDto loanRequestDto) {
		// Simulate a custom exception
		if ("error".equalsIgnoreCase(loanRequestDto.getLenderUid())) {

			throw new CustomException("Invalid lender UID provided", "LENDER_UID_ERROR");

		}

		log.info("Request: {}", loanRequestDto.toString());
		CommonResponse response = loanService.createLoan(loanRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("v2/request") // V2 API
	public ResponseEntity<CommonResponse> createLoanV2(@Valid @RequestBody LoanRequestDtoV2 loanRequestDto) {
		// Simulate a custom exception
		if ("error".equalsIgnoreCase(loanRequestDto.getLenderUid())) {
			throw new CustomException("Invalid lender UID provided", "LENDER_UID_ERROR");
		}

		CommonResponse response = loanService.createLoanV2(loanRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("v3/request") // V3 API
	public ResponseEntity<CommonResponse> createLoanV3(@Valid @RequestBody LoanRequestDtoV3 loanRequestDto) {
		// Simulate a custom exception
		if ("error".equalsIgnoreCase(loanRequestDto.getLenderUid())) {
			throw new CustomException("Invalid lender UID provided", "LENDER_UID_ERROR");
		}

		CommonResponse response = loanService.createLoanV3(loanRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/pan")
	public ResponseEntity<CommonResponse> processPan(@Valid @RequestBody PANRequestDto panRequestDto) {

		CommonResponse response = loanService.panRequest(panRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/aadhar")
	public ResponseEntity<CommonResponse> processAadhar(@Valid @RequestBody AadharRequestDto request) {
		CommonResponse response = loanService.aadharRequest(request);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/aadharOtp")
	public ResponseEntity<CommonResponse> processAadharOtp(
			@Valid @RequestBody AadharOtpRequestDto aadharOtpRequestDto) {
		CommonResponse response = loanService.aadharOtpRequest(aadharOtpRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/bankAccount")
	public ResponseEntity<CommonResponse> processbankAccount(
			@Valid @RequestBody BankAccountRequestDto bankAccountRequestDto) {
		CommonResponse response = loanService.bankAccountRequest(bankAccountRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/creditScore")
	public ResponseEntity<CommonResponse> processcreditScore(@Valid @RequestBody CibilRequestDto cibilRequestDto) {
		CommonResponse response = loanService.creditScoreRequest(cibilRequestDto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/userImage")
	public ResponseEntity<CommonResponse> uploadImage(@RequestParam("file") MultipartFile file,
			@RequestParam("sessionToken") String sessionToken) {
		// Delegate the image upload to the service class
		CommonResponse response = loanService.uploadUserImage(file, sessionToken);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/panImage")
	public ResponseEntity<CommonResponse> panImage(@RequestParam("file") MultipartFile file,
			@RequestParam("sessionToken") String sessionToken) {
		// Delegate the image upload to the service class
		CommonResponse response = loanService.uploadPanImage(file, sessionToken);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/aadharImage")
	public ResponseEntity<CommonResponse> aadharImage(@RequestParam("file") MultipartFile file,
			@RequestParam("sessionToken") String sessionToken) {
		// Delegate the image upload to the service class
		CommonResponse response = loanService.uploadAadharImage(file, sessionToken);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/bankStatementPdf")
	public ResponseEntity<CommonResponse> bankStatementPdf(@RequestParam("file") MultipartFile file,
			@RequestParam("sessionToken") String sessionToken) {
		CommonResponse response = loanService.uploadbankStatementPdf(file, sessionToken);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/processLoan")
	public ResponseEntity<CommonResponse> processLoan(@Valid @RequestBody ExtraInfoDto extraInfoDto) {
		CommonResponse response = loanService.processLoanRequest(extraInfoDto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	
	@PostMapping("/esign")
	public ResponseEntity<CommonResponse> esign(@Valid @RequestBody EsignDto esignDto ) {
		CommonResponse response = loanService.esign(esignDto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadPdf(@RequestParam String borrowerId) {

		byte[] pdfBytes = pdfService.generateDynamicLetterheadPdf(borrowerId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=HindonLoanAgreement.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
	}


	@PostMapping("/npc-response")
	public ResponseEntity<String> receiveNpcResponse(@RequestBody String responseXml) {
		System.out.println("Received KYC Response from NPCI: " + responseXml);
		return ResponseEntity.ok("NPCI Response Processed Successfully");
		// Parse the response and save it to the database
//	        KycResponse kycResponse = kycService.processNpcResponse(responseXml);

//	        if (kycResponse != null) {
//	            return ResponseEntity.ok("NPCI Response Processed Successfully");
//	        } else {
//	            return ResponseEntity.status(500).body("Failed to Process NPCI Response");
//	        }
	}
//	}

//	@PostMapping("/kyc")
//	public String sendKycRequest() {
//		return kycService.sendKycRequest();
//	}

	@PostMapping("/request")
	public String requestKyc() {
		try {
			String pr = "-----BEGIN PRIVATE KEY-----\r\n"
					+ "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCrdnxEpw2wXMK+\r\n"
					+ "HfQzBIia7v0uo83zWsCkQ+BodSReTGnE6Ix0ZVZRGqv57aMP4M0C08+o8JJ5YjxQ\r\n"
					+ "LZ4IEFXGl1dBdivUZomRvPEuU+gFFnqzlG1ogT3kI6p5LJ8BcFMqGDTIrm/p5QGe\r\n"
					+ "5GDjLYcuaabm6/Lcycait8hXIbNHcGEBSVl+0sZ5AaB1rR6/nVakGXyDvzLNV3c0\r\n"
					+ "WVJmi11h/IA/UACm+rpmL8xP5FkCg1khqaA1621lcUlMLVXV2lbQng5upnzqQbSG\r\n"
					+ "kCeElfKTRM8PnWWP7hGWvL+jxdHfLNh8qzbfYXCeRIxnQQzxKaIYrc050nrdgG/3\r\n"
					+ "NL8+wo7fAgMBAAECggEAL5Zh5PfsT69fCT7tAJ/Yfg+oSyKBTXI5lx2Tkco5Psa8\r\n"
					+ "cD8OhFt/umDJrELtB8IfhBJfRwcF0BSYorQWcSx/ce+c8vkmLvwKYF1tHquA8LCN\r\n"
					+ "e3vNZbzA/al8bccZll+jZUJ0m+H2A5dgfMXrsgF3zETcYqjHrcl+jLivLKeYx1GP\r\n"
					+ "FlwRO0BzlpoH1eWisX5aKEcjjbNbT5yDs6srCuqaN7tL0r+r+XvJ0oDuvYpUoInN\r\n"
					+ "r4hGiM81SFfyG6X9lw9Mo+38foBYpNqeMIruxjn0ycUuc+XjZFXAQEreiIFtGfnI\r\n"
					+ "fy88tLn15mwKzQONEQnN5pBnO0VkL7qOdaKxyrOjYQKBgQDtY/mZY6wcFT8kteev\r\n"
					+ "s9KNPdQKlf8b55T/FLOnih0SEgqGXTtZvfOqBigKIwXpsBWQK1PqlJiVuRGa2Ex6\r\n"
					+ "VieDPVlEib0Ys0tvQ9KKsnc6pvB8dNvgY+p/nIDHocqg+L6pynKiTNz9ISN/Rdk+\r\n"
					+ "MYmWFuPNMrkcAm0TBG7TRihi7wKBgQC453QYrAmsq0NikinVIExGx6JTG6JgzYai\r\n"
					+ "ZpHwam7QJGRETuIZujSMWk4ymGHuFAfiBitJxssbdk+EhDtBEscrEqXMPN135uq+\r\n"
					+ "MzqabQbutGl8zG8VBg2ccsCCnyMRxp+wjCOq0xTfzOAt1rUeDLoOrQrmdYS1uLWf\r\n"
					+ "6Wzv6AfTEQKBgQDNdtFq7LTjXZRYXsUX8wkSzGfBfc/exBLWsIFKSiUdJdZMM1eS\r\n"
					+ "NfE2wLtZArU0bP5M2ON5zoE+XX8aSYnv/K+YTLn9s0WiolRxCf+pogvGDQVqgu5o\r\n"
					+ "CbLGHpvrrWIm7wR/CsUrKmG/CTajCr6bsN6HtGoYiYVj88maQyT04e1EqwKBgCnb\r\n"
					+ "m5iKOZZxHswNspKgwSO0xbZypwuq+zOAbME0FunfkyMziFOyp3quZs1lWaX/utkb\r\n"
					+ "9Gi7K/eHjPC+znsouRWzHv1hOfGOwM1V44pZ7BvVk5vA29SyjhpAj/wB8npvsG7T\r\n"
					+ "Cq/9INiZFJbL6CxpTSVNXw5UxDovGk6dFSAqMrtBAoGACPdcXbUsbP2NCyBm/mas\r\n"
					+ "mGLXq7Hv9j42CiZac5BenJvtCG+5CsF+lENk+NHvVgZOg15VPWY+nBXOp/r5Miwy\r\n"
					+ "aZHD4PHvhNGB24qMIYyAVPkLoKjt20qUqX85UtAEmWeB8xoSbz4BFzy2jRIdB5n2\r\n"
					+ "96Un2UC4lh47F4jEe2TLp+o=\r\n" + "-----END PRIVATE KEY-----\r\n" + "";
			return kycService.generateSignedXML(pr);
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}

	@PostMapping("/send")
	public String sendKycRequest() {
		String url = "https://globalcertzone.npci.org.in/aeps/ReqKycData/2.0/urn:txnid:HML12345675565273665474887";

		String xmlData = """
								<?xml version="1.0" encoding="UTF-8" standalone="no"?><ns2:ReqKycData xmlns:ns2="http://npci.org/upi/schema/">
				 <Head msgId="HML12345675565273665474887" orgId="202771" prodType="AEPS" ts="2025-02-25T18:15:02.879842500" ver="2.0"/>
				 <Txn id="HML12345675565273665474887" note="Testing Request API" refId="474887" refUrl="https://www.npci.org.in/" ts="2025-02-25T18:15:02.879842500" type="KycData">
				 <RiskScores/>
				 </Txn>
				<Signature xmlns="http://www.w3.org/2000/09/xmldsig#"><SignedInfo><CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/><SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/><Reference URI=""><Transforms><Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/></Transforms><DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/><DigestValue>JpHd7/PRDnNEvmXHoD0dpO6vCB/5x3EarHRvAYC+gmo=</DigestValue></Reference></SignedInfo><SignatureValue>PDZDGWOfB0OWjTjCOBqTy26gXoFtH8haTsXo4WAbp2/9x2STQRjSyjkW+wR3P8AIodU36Tt1DWEI&#13;
				AU8KCkiyoiaTs1frwBjqc/jgoz385SsW7bcO9ZZlDvlGp3cK6H0OZvmvwSlPCSmcXBVYPzrcFf3+&#13;
				j459HLEnjY3Palipye5NvKOpWfQ0ycktwHd/Apa7ZxvzaBFK87hvkE/eQi3HvUN6LWSwQiuiC3OR&#13;
				ije2EsS8967aPTPy38C+lliYt5ngRWs54Y2FjGMmPQBTDeh1qCjFstvxqmGn2Z1DT1L8Vi8MVyKf&#13;
				XoSPo3uDV+MSPVlx4iLdv5RgeJUl1aIk1Mi2RQ==</SignatureValue><KeyInfo><KeyValue><RSAKeyValue><Modulus>q3Z8RKcNsFzCvh30MwSImu79LqPN81rApEPgaHUkXkxpxOiMdGVWURqr+e2jD+DNAtPPqPCSeWI8&#13;
				UC2eCBBVxpdXQXYr1GaJkbzxLlPoBRZ6s5RtaIE95COqeSyfAXBTKhg0yK5v6eUBnuRg4y2HLmmm&#13;
				5uvy3MnGorfIVyGzR3BhAUlZftLGeQGgda0ev51WpBl8g78yzVd3NFlSZotdYfyAP1AApvq6Zi/M&#13;
				T+RZAoNZIamgNettZXFJTC1V1dpW0J4ObqZ86kG0hpAnhJXyk0TPD51lj+4Rlry/o8XR3yzYfKs2&#13;
				32FwnkSMZ0EM8SmiGK3NOdJ63YBv9zS/PsKO3w==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue></KeyValue></KeyInfo></Signature></ns2:ReqKycData>

								""";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> request = new HttpEntity<>(xmlData, headers);

		String response = restTemplate.postForObject(url, request, String.class);
		System.out.println("url" + url);
		System.out.println("xmlData" + xmlData);
		System.out.println("response" + response);
		return response;
	}

}
