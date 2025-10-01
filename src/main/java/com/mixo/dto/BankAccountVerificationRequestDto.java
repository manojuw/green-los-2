package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BankAccountVerificationRequestDto {

	@NotBlank(message = "Uid cannot be blank")
	private String uid;

	@NotBlank(message = "Bank account cannot be blank")
	@Size(min = 6, message = "Bank Account should have at least 6 characters")
	private String bankAccount;

	@NotBlank(message = "IFSC code cannot be blank")
	@Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
	private String ifscCode;

	@NotBlank(message = "Account type cannot be blank")
	private String accountType;

}
