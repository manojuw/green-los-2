package com.mixo.dto;

import lombok.Data;

@Data
public class LoanResponseDto {

	private String message;
	private String loanRefNo;
	private String nextStep;
	private String sessionToken;
	private Long sessionActiveTime;

	private Object data;

}
