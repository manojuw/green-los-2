package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoanStatusRequestDto {

	@NotBlank(message = "borrowerUid cannot be blank")
	private String borrowerUid;

	@NotBlank(message = "customerUid cannot be blank")
	private String customerUid;

}
