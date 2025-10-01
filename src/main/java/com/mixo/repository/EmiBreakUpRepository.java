package com.mixo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.EmiBreakUp;

public interface EmiBreakUpRepository extends JpaRepository<EmiBreakUp, Long> {

	List<EmiBreakUp> findByBorrowerUid(String uid);

}
