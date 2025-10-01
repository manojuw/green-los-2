package com.mixo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LoanRequestDtoV3 {

	@NotBlank(message = "Lender UID cannot be blank")
	private String lenderUid;

	@NotBlank(message = "Product ID cannot be blank")
	private String productId;

	@NotBlank(message = "userUniqueId cannot be blank")
	private String userUniqueId;

	@NotNull(message = "Loan amount cannot be null")
	@Positive(message = "Loan amount must be greater than zero")
	private Double loanAmount;

	@Email(message = "Invalid email address format")
	@NotBlank(message = "Email ID cannot be blank")
	private String emailId;

	@Pattern(regexp = "\\d{10}", message = "Mobile number must be a 10-digit numeric value")
	private String mobileNo;

	@NotBlank(message = "Purpose of loan cannot be blank")
	private String purposeOfLoan;

	@NotNull(message = "Total salary cannot be null")
	@PositiveOrZero(message = "Total salary must be zero or a positive value")
	private Double totalSalary;

	@NotBlank(message = "Employment type cannot be blank")
	private String employmentType;

	@NotBlank(message = "Employer name cannot be blank")
	private String employerName;

//	@NotBlank(message = "Relationship type cannot be blank")
	private String relationshipType;

//	@NotBlank(message = "Related person's name cannot be blank")
	private String relatedPersonName;

	@NotBlank(message = "Marital status cannot be blank")
	@Pattern(regexp = "^(Married|Single)$", message = "Marital Status must be either Married, Single")
	private String maritalStatus;

	@NotBlank(message = "Gender cannot be blank")
	@Pattern(regexp = "^(Male|Female)$", message = "Gender must be either Male, Female")
	private String gender;
	@NotNull(message = "Date of birth cannot be null")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;
	@NotNull(message = "EMI time cannot be null")
	@Min(value = 1, message = "Number of EMI must be at least 1 ")
	private Integer noOfEMI;
	@NotBlank(message = "Name as per PAN cannot be blank")
	private String nameAsPerPan;

	@NotNull(message = "Rate of interest cannot be null")
	@PositiveOrZero(message = "Rate of interest must be zero or a positive value")
	private Double rateOfInterest;

	@NotNull(message = "Date of EMI cannot be null")
	@Future(message = "Date of EMI must be in the future")
	private LocalDate dateOfEmi;

	private Double emiAmount;

}
