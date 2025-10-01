package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RuleEngine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String uid;
	private String productId;

	private String cibilStatus;
	private String cibilValue;

	private String firstYearDpdStatus;
	private String firstYearDpdValue;

	private String secondYearDpdStatus;
	private String secondYearDpdValue;

	private String minAgeStatus;
	private String minAgeValue;

	private String maxAgeStatus;
	private String maxAgeValue;

	@CreationTimestamp
	private LocalDateTime createdOn;

	@UpdateTimestamp
	private LocalDateTime updatedOn;

	private String createdBy;
	private String updatedBy;

}
