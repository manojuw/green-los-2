package com.mixo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerNach;

public interface BorrowerNachRepository extends JpaRepository<BorrowerNach, Long> {
	
	List<BorrowerNach> findByBorrowerUid(String borrowerUid);
	
	BorrowerNach findBySubscriptionId(String subscriptionId);

}
