package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AadharRequestDto {
	@NotBlank(message = "Session token cannot be blank")
	private String sessionToken;
	@NotBlank(message = "Aadhar Number is required")
	@Size(min = 12, message = "Aadhar Number should have at least 12 characters")
	@Pattern(regexp = "[0-9]{12}", message = "Invalid Aadhar Number")
	private String aadharNumber;
}