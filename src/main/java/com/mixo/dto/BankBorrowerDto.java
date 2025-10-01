package com.mixo.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class BankBorrowerDto {
	
	private String borrowerUid;

	private String bankAccountNo;
	private String AccountType;
	private String ifscCode;
	private String nameAtBank;
	private String bankBranchName;

	@Lob
	private String bankResponse;

}
