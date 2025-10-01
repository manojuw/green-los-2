package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class BorrowerCibil {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String dpd;
	private String cibilScore;
	private String activeCredit;
	private String suitFile;
	private String settledLoan;
	private String writeOff;
	private String firstYearDPD;
	private String secondYearDPD;
	private boolean ntc;
	private String caisAccountDetails;

	@Lob
	private String cibilResponse;

	@CreationTimestamp
	private LocalDateTime createdOn;

}
