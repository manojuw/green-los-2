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
public class BorrowerBank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String bankAccountNo;
	private String AccountType;
	private String ifscCode;
	private String nameAtBank;
	private String bankBranchName;

	@Lob
	private String bankResponse;

	@CreationTimestamp
	private LocalDateTime createdOn;

}
