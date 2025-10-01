package com.mixo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LendingRequestDtoV1 {

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
}
