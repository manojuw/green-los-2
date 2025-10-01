package com.mixo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Nbfc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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
	@Column(nullable = true)
    private String organisationLogoPath;

    @Column(nullable = true)
    private String authorisedSignatoryPath;

    @Column(nullable = true)
    private String organisationDocumentPath;
	

	
	
	

}
