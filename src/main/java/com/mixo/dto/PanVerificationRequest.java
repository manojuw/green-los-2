package com.mixo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PanVerificationRequest {

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

}
