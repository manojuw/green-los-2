package com.mixo.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class CibilBorrowerDto {

	private String borrowerUid;

	private String dpd;
	private String cibilScore;
	private String activeCredit;
	private String suitFile;
	private String settledLoan;
	private String writeOff;
	private String firstYearDPD;
	private String secondYearDPD;
	private boolean ntc;
	private String caisAccountDetails;

	@Lob
	private String cibilResponse;

}
