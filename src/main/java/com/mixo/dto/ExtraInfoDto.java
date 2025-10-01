package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExtraInfoDto {
	@NotBlank(message = "Session token cannot be blank")
	private String sessionToken;

	private String udf1;

	private String udf2;

	private String udf3;

	private String udf4;

	private String udf5;
	@NotNull(message = "Consent is required")
	private Boolean consentForLoan;

	@NotBlank(message = "secondaryAddressLine1 cannot be blank")
	private String secondaryAddressLine1;
	@NotBlank(message = "secondaryArea cannot be blank")
	private String secondaryArea;
	@NotBlank(message = "secondaryCity cannot be blank")
	private String secondaryCity;
	@NotBlank(message = "secondaryState cannot be blank")
	private String secondaryState;
	@NotBlank(message = "secondaryLandmark cannot be blank")
	private String secondaryLandmark;
	@NotBlank(message = "secondaryPinCode cannot be blank")
	private String secondaryPinCode;

	private String webhookUrl;

}