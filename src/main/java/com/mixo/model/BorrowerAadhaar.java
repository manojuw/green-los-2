package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class BorrowerAadhaar {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String borrowerUid;
	
	private String aadharNumber;
	private String nameInAadharCard;
	private String dateofBirth;
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
	private String aadharRawXml;
	private String careOfInAadhar;
	private String genderInAadhar;
	
	private String aadharRefId;
	
	private Boolean isVerified;
	
	@Lob
	private String aadharResponse;

	@CreationTimestamp
	private LocalDateTime createdOn;
	
	@UpdateTimestamp
	private LocalDateTime updatedOn;

}
