package com.mixo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LendingRequestDtoV2 {

	@NotBlank(message = "Lender UID cannot be blank")
	private String lenderUid;

	@NotBlank(message = "Product ID cannot be blank")
	private String productId;

	@NotNull(message = "Loan amount cannot be null")
	@Positive(message = "Loan amount must be greater than zero")
	private Double loanAmount;

	@NotBlank(message = "Purpose of loan cannot be blank")
	private String purposeOfLoan;

	@PositiveOrZero(message = "totalMonthlySalary must be zero or a positive value")
	private Double totalMonthlySalary;

	@NotNull(message = "Rate of interest cannot be null")
	@PositiveOrZero(message = "Rate of interest must be zero or a positive value")
	private Double rateOfInterest;

	@NotNull(message = "consentForCibil is required")
	private Boolean consentForCibil;

	@NotBlank(message = "uid cannot be blank")
	private String uid;

	@NotNull(message = "EMI time cannot be null")
	@Min(value = 1, message = "Number of EMI must be at least 1 ")
	private Integer noOfEmi;

	@NotNull(message = "Date of EMI cannot be null")
	@Future(message = "Date of EMI must be in the future")
	private LocalDate dateOfEmi;

	@NotBlank(message = "webhookUrl cannot be blank")
	private String webhookUrl;

	private String udf1;

	private String udf2;

	private String udf3;

	private String udf4;

	private String udf5;

	@NotBlank(message = "secondaryAddressLine1 cannot be blank")
	private String secondaryAddressLine1;
	@NotBlank(message = "secondaryArea cannot be blank")
	private String secondaryArea;
	@NotBlank(message = "secondaryCity cannot be blank")
	private String secondaryCity;
	@NotBlank(message = "secondaryState cannot be blank")
	private String secondaryState;
	@NotBlank(message = "secondaryLandmark cannot be blank")
	private String secondaryLandmark;
	@NotBlank(message = "secondaryPinCode cannot be blank")
	private String secondaryPinCode;
	@NotBlank(message = "nameOfEmployer cannot be blank")
	private String nameOfEmployer;
	@NotBlank(message = "designation cannot be blank")
	private String designation;
}
