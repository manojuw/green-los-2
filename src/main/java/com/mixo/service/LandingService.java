package com.mixo.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

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
import com.mixo.dto.VerificationResponse;

import jakarta.validation.Valid;

public interface LandingService {

	VerificationResponse verifyPan(PanVerificationRequest request);

	VerificationResponse aadharRequest(AadharVerificationRequestDto request);

	VerificationResponse aadharOtpRequest(@Valid AadharVerificationOtpRequestDto request);

	VerificationResponse bankVerification(@Valid BankAccountVerificationRequestDto bankAccountRequestDto);

	VerificationResponse uploadUserImage(MultipartFile file, String uid);

	VerificationResponse lendingRequestV1(@Valid LendingRequestDtoV1 lendingRequestDtoV1);

	VerificationResponse lendingRequestV2(@Valid LendingRequestDtoV2 lendingRequestDtoV2);

	Map<String, Object> getNextStep(String uid);

	VerificationResponse esign(@Valid EsignVDto esignVDto);

	VerificationResponse enach(@Valid EsignVDto esignVDto);

	VerificationResponse getCustomerUid(@Valid CustomerUidDto request);

	VerificationResponse getLoanStatus(@Valid LoanStatusRequestDto request);

	VerificationResponse fullKyc(@Valid FullKycDto fullKycDto);

	byte[] getEsign(@Valid EsignDownloadRequestDto request);

	VerificationResponse uploadEsignedPdf(MultipartFile file, String borrowerId);

	VerificationResponse aadharRequestv2( AadharVerificationRequestDto request);

	VerificationResponse aadharStatusRequestv2(@Valid AadharVerificationRequestDto request);

	VerificationResponse uploadLogPdf(MultipartFile file, String borrowerId);

}
