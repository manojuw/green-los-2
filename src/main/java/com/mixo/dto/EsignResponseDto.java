package com.mixo.dto;

import lombok.Data;

@Data
public class EsignResponseDto {

	private String status;
	private String message;
	private String signingUrl;

}
