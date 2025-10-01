package com.mixo.dto;

import org.apache.commons.lang3.StringUtils;

public enum LoanStatus {
	
	LOAN_INITIATE			("LOAN_INITIATE"),
	LOAN_SUCCESSFUL			("LOAN_SUCCESSFUL"),
	LOAN_FAILED				("LOAN_FAILED"),
	
	;

	private final String value;

	public String getValue() {
		return value;
	}
	
	private LoanStatus(String value) {
		this.value = value;
	}
	
	public static LoanStatus getInstance(String name){
		if(StringUtils.isBlank(name)) return null;
		LoanStatus[] loanStatusArr = LoanStatus.values();
		for(LoanStatus loanStatus : loanStatusArr){
			if(loanStatus.getValue().equals(name)){
				return loanStatus;
			}
		}
		return null;
	}
}
