package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EsignVDto {

	@NotBlank(message = "borrowerUid cannot be blank")
	private String borrowerUid;

	@NotBlank(message = "returnUrl cannot be blank")
	private String returnUrl;

}
