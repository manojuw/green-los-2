package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BorrowerNach {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String nachId;

	private boolean nachStatus;

	private String planName;

	private Double planRecurringAmount;

	private String subscriptionId;

	private Double planMaxAmount;

	private String subscriptionStatus;

	private String subscriptionSessionId;
	
	private String nachUrl;
	
	private String nachReturnUrl;

	@CreationTimestamp
	private LocalDateTime createdOn;

}
