package com.mixo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDTO {

	@NotEmpty(message = "Username is required")
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String userName;

	@NotEmpty(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String password;

	@NotEmpty(message = "Role is required")
	private String role;

	@NotEmpty(message = "Reseller ID is required")
	@Size(min = 20, max = 20, message = "Reseller ID must be 20 characters")
	private String resellerId;

	@NotEmpty(message = "Business Name is required")
	@Size(min = 3, max = 50, message = "Business Name must be between 3 and 50 characters")
	private String businessName;

	@NotEmpty(message = "Email is required")
	@Email(message = "Enter a valid email")
	private String emailId;

	@NotEmpty(message = "Mobile number is required")
	@Pattern(regexp = "[0-9]{10}", message = "Mobile number must be 10 digits")
	private String mobile;

	// Getters and setters

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
