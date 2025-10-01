package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerUidDto {

	@Pattern(regexp = "\\d{10}", message = "Mobile number must be a 10-digit numeric value")
	private String mobileNo;

	@NotBlank(message = "PAN number cannot be blank")
	@Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format 5 uppercase letters, followed by 4 digits, and ending with 1 uppercase letter (e.g., ABCDE1234F)")
	private String panNumber;

}
