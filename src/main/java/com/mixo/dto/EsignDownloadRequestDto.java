package com.mixo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EsignDownloadRequestDto {

	@NotBlank(message = "borrowerUid cannot be blank")
	private String borrowerUid;

}
