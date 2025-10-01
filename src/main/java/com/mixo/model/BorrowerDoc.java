package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BorrowerDoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String eSignReqId;
	private String accessToken;
	private String mobileNumber;
	private String digioId;
	private String eSignRedirectUrl;
	private boolean eSignStatus;

	private String userImage;
	private String panImage;
	private String aadharImage;
	private String bankStatementPdf;

	@CreationTimestamp
	private LocalDateTime createdOn;

}
