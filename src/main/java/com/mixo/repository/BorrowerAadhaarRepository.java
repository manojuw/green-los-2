package com.mixo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerAadhaar;

public interface BorrowerAadhaarRepository extends JpaRepository<BorrowerAadhaar, Long> {

	BorrowerAadhaar findByBorrowerUid(String sessionToken);
	
	List<BorrowerAadhaar> findByIsVerifiedAndUpdatedOnBetween(Boolean isVerified, LocalDateTime from, LocalDateTime to);


}
