package com.mixo.dto;

import lombok.Data;

@Data
public class DocBorrowerDto {
	
	private String borrowerUid;

	private String eSignReqId;
	private String eSignDocId;
	private String eSignRedirectUrl;
	private boolean eSignStatus;

	private String userImage;
	private String panImage;
	private String aadharImage;
	private String bankStatementPdf;

}
