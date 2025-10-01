package com.mixo.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class PanBorrowerDto {

	private String borrowerUid;

	private String panNumber;
	private String nameInPanNumber;
	private String panCardFlag;

	@Lob
	private String panResponse;

}
