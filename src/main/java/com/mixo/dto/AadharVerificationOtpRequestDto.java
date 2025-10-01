package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AadharVerificationOtpRequestDto {

	@NotBlank(message = "uid cannot be blank")
	private String uid;
	@NotBlank(message = "OTP is required")
	@Size(min = 6, message = "OTP should have at least 6 Numbers")
	@Pattern(regexp = "[0-9]{6}", message = "Invalid OTP Format")
	private String aadharOtp;

}
