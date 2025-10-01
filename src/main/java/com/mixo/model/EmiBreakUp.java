package com.mixo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class EmiBreakUp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;
	private String mobileNo;
	private int financeId;
	private String loanAggrement;

	private int installmentNo;
	private String dueDate;
	private double dueAmount;
	private String paymentStatus;

}
