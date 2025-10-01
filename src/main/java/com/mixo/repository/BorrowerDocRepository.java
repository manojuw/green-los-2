package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerDoc;

public interface BorrowerDocRepository extends JpaRepository<BorrowerDoc, Long> {

	BorrowerDoc findByBorrowerUid(String borrowerId);

	BorrowerDoc findByDigioId(String esignDocId);
	
	BorrowerDoc findByAccessToken(String accessToken);

}
