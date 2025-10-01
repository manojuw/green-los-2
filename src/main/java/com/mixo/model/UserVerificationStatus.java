package com.mixo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_verification_status")
@Data
public class UserVerificationStatus {
	
	@Id
    private String uid;  // Unique identifier for the user

    private boolean panVerified;
    private boolean aadharVerified;
    private boolean bankVerified;
    private boolean imageUploaded;

}
