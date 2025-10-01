package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CibilRequestDto {
	
	@NotBlank(message = "Session token cannot be blank")
	private String sessionToken;
	
	@NotNull(message = "Consent is required")
	private Boolean consent;

}
