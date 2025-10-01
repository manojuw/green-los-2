package com.mixo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class BorrowerPan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String borrowerUid;

	private String panNumber;
	private String nameInPanNumber;
	private String panCardFlag;

	@Lob
	private String panResponse;

	@CreationTimestamp
	private LocalDateTime createdOn;

}
