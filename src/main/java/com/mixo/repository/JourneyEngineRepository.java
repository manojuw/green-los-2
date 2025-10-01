package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.JourneyEngine;

public interface JourneyEngineRepository extends JpaRepository<JourneyEngine, Long> {

	JourneyEngine findByUidAndProductId(String uid, String productId);

}
