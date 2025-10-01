package com.mixo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String uid;

	@Column(nullable = false)
	private String productType;

	@Column(nullable = false)
	private String loanType;

	@Column(nullable = false)
	private String productId;

	@Column(nullable = false)
	private String schemeName;

	@Column(nullable = false)
	private String schemeId;

	@Column(nullable = false)
	private String disbursementModel;

	@Column(nullable = false)
	private String partnershipType;

	@Column(nullable = false)
	private String partnershipRatio;

	@Column(nullable = false)
	private String contractedIrr;

	@Column(nullable = false)
	private String interestType;

	private LocalDate partnershipStartDate;

	private LocalDate partnershipEndDate;

	@Column(nullable = false)
	private String partnershipDisbursementLimit;

	@Column(nullable = false)
	private String monthlyDisbursementLimit;

	@Column(nullable = false, length = 4)
	private String loanAgreementPrefix;

	@Column(nullable = false)
	private String emiFrequency;

	@Column(nullable = false)
	private Double minAmount;

	@Column(nullable = false)
	private Double maxAmount;

	@Column(nullable = false)
	private Double lateFeeCharge;

	@Column(nullable = false)
	private Double minInterestRate;

	@Column(nullable = false)
	private Double maxInterestRate;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	private Integer defaultDate;

	private Double defaultInterestRate;

	@Column
	private String minTenure;

	@Column
	private String maxTenure;

	@Column
	private String status;

	private String kfs;

	private Double processingFee;

	private Double insuranceCharges;

	private Double overdueInterest;

	private Double penalCharges;

	private Double bounceCharges;

	private Double swapCharges;

	private Double perPaymentCharges;

	private Double statementCharges;

	private Double emiFollowupCharges;

	private Double stampDuty;

	private Double otherCharges;

	@Column
	private String apiFlow;

	private String createdBy;
	private String updatedBy;

	private int leadSourceId;

	private String extraFields;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {

		updatedAt = LocalDateTime.now();
	}

	// Getters and Setters

}
