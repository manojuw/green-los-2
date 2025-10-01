package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.UserVerificationStatus;

public interface UserVerificationStatusRepository extends JpaRepository<UserVerificationStatus, String> {
}
