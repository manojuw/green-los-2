package com.mixo.service;

import java.util.Optional;

import org.json.JSONObject;

import com.mixo.dto.AadharVerificationRequestDto;
import com.mixo.dto.PanVerificationRequest;
import com.mixo.model.Borrower;
import com.mixo.model.BorrowerAadhaar;

public interface ApiService {

	String verifyPAN(String panNumber, Optional<Borrower> borrower);

	String getCreditScore(Borrower borrowerObj);

	String aadharOtpApi(String aadharNumber, Optional<Borrower> borrower);

	String verifyAadhar(String aadharOtp, Optional<Borrower> borrower, String string);

	String verifyBankAccount(JSONObject jsonObject);

	String esign(Borrower borrowerObj, String base64Pdf);

	String verifyPANRequest(PanVerificationRequest request);

	String aadharOtpApi(AadharVerificationRequestDto request);

	String verifyAadhar(String aadharOtp, String string);

	String enach(Borrower borrowerDetails, String string);

	String esignV2(Borrower borrowerObj, String base64Pdf);

	String aadharOtpApiV2(AadharVerificationRequestDto request);

	void checkAadharStatus(BorrowerAadhaar borrowerAadhaar);

	String esignV2forDirectSign(Borrower borrowerObj, String base64Pdf);

}
