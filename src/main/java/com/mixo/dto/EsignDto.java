package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EsignDto {
	@NotBlank(message = "Session token cannot be blank")
	private String sessionToken;

	private String returnUrl;

}