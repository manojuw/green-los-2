package com.mixo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class JourneyEngine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String uid;
	private String productId;
	private int cibil;
	private int pan;
	private int aadhaar;
	private int aadhaarOtp;
	private int penny;
	private int bankStatement;
	private int userImage;
	private int panImage;
	private int aadhaarImage;
	private int processLoan;
	private int loanRequest = 0;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	private String createdBy;
	private String updatedBy;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {

		updatedAt = LocalDateTime.now();
	}

}
