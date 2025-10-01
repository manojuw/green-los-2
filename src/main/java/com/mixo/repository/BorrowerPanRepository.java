package com.mixo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.BorrowerPan;

public interface BorrowerPanRepository extends JpaRepository<BorrowerPan, Long> {

	BorrowerPan findByBorrowerUid(String borrowerUid);

	List<BorrowerPan> findByPanNumber(String panNumber);

}
