package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerCibil;

public interface BorrowerCibilRepository extends JpaRepository<BorrowerCibil, Long> {

	BorrowerCibil findByBorrowerUid(String borrowerUid);

	BorrowerCibil findTopByBorrowerUidOrderByCreatedOnDesc(String customerId);

}
