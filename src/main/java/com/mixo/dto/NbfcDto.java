package com.mixo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NbfcDto {
	
	private String uid;
	private String brandName;
	private String coLenderName;
	private String nbfcName;
	private String organisationEmail;
	private String website;
	private String address;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	private String colenderRedressalOfficerName;
	private String colenderRedressalOfficerEmail;
	private String colenderRedressalOfficerPhone;
	private String gstNumber;
	private String panNumber;
	private String cinNumber;
	private String nbfcRegistrationNumber;
	private String bankName;
	private String accountNumber;
	private String bankBranch;
	private String ifscCode;
	private String micr;
	private String authorisedPersonName;
	private String authorisedPersonEmail;
	private String authorisedPersonMobile;
	private String spoC;
	private String userName;
	private String password;
	
	@NotNull(message = "Organisation logo is required")
	private String organisationLogoPath;

	@NotNull(message = "Authorised signatory document is required")
	private String authorisedSignatoryPath;

	@NotNull(message = "Organisation document is required")
	private String organisationDocumentPath;

}
