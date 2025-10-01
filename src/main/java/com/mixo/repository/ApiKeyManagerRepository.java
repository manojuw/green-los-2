package com.mixo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.ApiKeyManager;

public interface ApiKeyManagerRepository extends JpaRepository<ApiKeyManager, Long> {

	Optional<ApiKeyManager> findByUid(String uid);

}
