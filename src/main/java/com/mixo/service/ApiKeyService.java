package com.mixo.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mixo.model.ApiKey;
import com.mixo.repository.ApiKeyRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiKeyService {

	private final ApiKeyRepository apiKeyRepository;
    private String currentApiKey;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
        init();
    }
    
    

    @PostConstruct
    private void init() {
        Optional<ApiKey> existingKey = apiKeyRepository.findTopByOrderByCreatedAtDesc();
        if (existingKey.isPresent()) {
            this.currentApiKey = existingKey.get().getApiKey();
        } else {
            rotateApiKey(); // generate new key if none exists or it's expired
        }
    }

    public String getApiKey() {
        return currentApiKey;
    }

    public void rotateApiKey() {
        String newKey = UUID.randomUUID().toString().replace("-", "");
        ApiKey apiKey = new ApiKey();
        apiKey.setApiKey(newKey);
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
        this.currentApiKey = newKey;
        log.info("Rotated API Key: {}", newKey);
    }
}
