package com.mixo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findTopByOrderByCreatedAtDesc();
}