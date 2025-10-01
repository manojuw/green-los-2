package com.mixo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FullKycDto {

	@Email(message = "Invalid email address format")
	@NotBlank(message = "Email ID cannot be blank")
	private String emailId;

	@Pattern(regexp = "\\d{10}", message = "Mobile number must be a 10-digit numeric value")
	private String mobileNo;

	@NotBlank(message = "Marital status cannot be blank")
	@Pattern(regexp = "^(Married|Single)$", message = "Marital Status must be either Married, Single")
	private String maritalStatus;

	@NotBlank(message = "Gender cannot be blank")
	@Pattern(regexp = "^(Male|Female)$", message = "Gender must be either Male, Female")
	private String gender;
	@NotNull(message = "Date of birth cannot be null")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@NotBlank(message = "Name as per PAN cannot be blank")
	private String nameAsPerPan;

	@NotBlank(message = "PAN number cannot be blank")
	@Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format 5 uppercase letters, followed by 4 digits, and ending with 1 uppercase letter (e.g., ABCDE1234F)")
	private String panNumber;

	@NotBlank(message = "Aadhar Number is required")
	@Size(min = 12, message = "Aadhar Number should have at least 12 characters")
	@Pattern(regexp = "[0-9]{12}", message = "Invalid Aadhar Number")
	private String aadharNumber;

	private String nameInAadharCard;
	private String countryInAadhar;
	private String stateInAadhar;
	private String distInAadhar;
	private String subdistInAadhar;
	private String poInAadhar;
	private String vtcInAadhar;
	private String locInAadhar;
	private String streetInAadhar;
	private String houseInAadhar;
	private String landmarkInAadhar;
	private String zipInAadhar;
	private String careOfInAadhar;
	private String genderInAadhar;
	
	@NotBlank(message = "Bank account cannot be blank")
	@Size(min = 6, message = "Bank Account should have at least 6 characters")
	private String bankAccount;

	@NotBlank(message = "IFSC code cannot be blank")
	@Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
	private String ifscCode;

	@NotBlank(message = "Account type cannot be blank")
	private String accountType;

}
