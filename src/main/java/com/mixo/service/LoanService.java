package com.mixo.service;

import org.springframework.web.multipart.MultipartFile;

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
import com.mixo.utils.CommonResponse;

import jakarta.validation.Valid;

public interface LoanService {

	CommonResponse createLoan(@Valid LoanRequestDto loanRequestDto);

	CommonResponse panRequest(PANRequestDto panRequestDto);

	CommonResponse aadharRequest(AadharRequestDto request);

	CommonResponse aadharOtpRequest(@Valid AadharOtpRequestDto aadharOtpRequestDto);

	CommonResponse bankAccountRequest(@Valid BankAccountRequestDto bankAccountRequestDto);

	CommonResponse creditScoreRequest(@Valid CibilRequestDto cibilRequestDto);

	CommonResponse uploadUserImage(MultipartFile file, String sessionToken);

	CommonResponse uploadPanImage(MultipartFile file, String sessionToken);

	CommonResponse uploadAadharImage(MultipartFile file, String sessionToken);

	CommonResponse uploadbankStatementPdf(MultipartFile file, String sessionToken);

	CommonResponse processLoanRequest(@Valid ExtraInfoDto extraInfoDto);

//	String generateHtmlContent(String sessionToken);

	CommonResponse createLoanV2(@Valid LoanRequestDtoV2 loanRequestDto);

	CommonResponse createLoanV3(@Valid LoanRequestDtoV3 loanRequestDto);

	CommonResponse esign(@Valid EsignDto esignDto);

}
