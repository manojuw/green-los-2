package com.mixo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mixo.dto.LoanStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Borrower {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String lenderUid;
	private String lenderName;
	private String lenderBrandName;
	private String lenderAuthorityEmail;
	private String lenderAuthorityName;
	private String LenderAuthorityMobileNo;

	private String schemeId;

	private String fullName;

	private String gender;
	private String emiFrequency;

	private Double loanAmount;
	private Double emiRate;
	private int emiTime;
	private Double emiAmount;
	private Double sectionAmount;
	private String productId;
	private String loanType;

	private String employmentType;
	private String emailId;
	private String mobileNo;

	private String loanAggrement;
	private String purposeOfLoan;
	private String natureOfLoan;
	private Double totalProcessingFee;
	private Double totalLoanAmount;
	private Double totalInterest;
	private String responseCode;
	private String responseMessage;
	private Double fees;
	private String emiDate;

	private LocalDate dateOfBirth;

	private Double totalSalary;
	private String customerId;
	private int financeId;

	private int leadSourceId;

	private int productTypeId;

	private String relationshipType;
	private String relatedPersonName;

	private String maritalStatus;

	private String nextStep;

	private String udf1;
	private String udf2;
	private String udf3;
	private String udf4;
	private String udf5;

	private String secondaryAddressLine1;
	private String secondaryArea;
	private String secondaryCity;
	private String secondaryState;
	private String secondaryLandmark;
	private String secondaryPinCode;

	private Boolean consentForLoan;

	private String userUniqueId;

	private String webhookUrl;

	private LoanStatus loanStatus;

	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime updatedOn;

	private boolean extraFields;

	private String customerLosId;

	private String kycStep;

	private Long loanDays;

	private Double apr;

	private String employerName;

	private String designation;

	private Boolean esignStatus = false;

	private Boolean eNachStatus = false;

}
