package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerBank;

public interface BorrowerBankRepository extends JpaRepository<BorrowerBank, Long> {

	BorrowerBank findByBorrowerUid(String borrowerUid);

}
