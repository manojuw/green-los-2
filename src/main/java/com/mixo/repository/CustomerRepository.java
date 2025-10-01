package com.mixo.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Customer findByPanIdentifier(String panIdentifier);

	Customer findByPanNumber(String panNumber);

	Customer findByUid(String uid);

	Customer findByEmailId(String emailId);

	Customer findByMobileNo(String mobileNo);

	Page<Customer> findByUid(String uid, Pageable pageable);

	Page<Customer> findByCreatedAtBetween(LocalDateTime fromDates, LocalDateTime toDates, Pageable pageable); // findByMobileNo(String
																												// mobileNo,
																												// Pageable
																												// pageable);

}
