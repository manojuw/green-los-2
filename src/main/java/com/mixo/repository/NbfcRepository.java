package com.mixo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.Nbfc;

public interface NbfcRepository extends JpaRepository<Nbfc, Long> {

	Optional<Nbfc> findByBrandName(String brandName);
	
	Optional<Nbfc> findByUid(String uid);

}
