package com.mixo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.RuleEngine;

public interface RuleEngineRepository extends JpaRepository<RuleEngine, Long> {
	
	Optional<RuleEngine> findByUidAndProductId(String uid, String productId);

}
