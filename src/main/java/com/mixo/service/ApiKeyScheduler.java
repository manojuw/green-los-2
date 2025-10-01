package com.mixo.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mixo.repository.ApiKeyRepository;

@Configuration
@EnableScheduling
public class ApiKeyScheduler {

	private final ApiKeyService apiKeyService;

	public ApiKeyScheduler(ApiKeyService apiKeyService) {
		this.apiKeyService = apiKeyService;
	}

	@Autowired
	ApiKeyRepository apiKeyRepository;

//	@Scheduled(fixedRate = 7 * 24 * 60 * 60 * 1000) // Every 30 days
	public void rotateApiKey() {
		if (Duration.between(apiKeyRepository.findTopByOrderByCreatedAtDesc().get().getCreatedAt(), LocalDateTime.now())
				.toDays() >= 30) {
			apiKeyService.rotateApiKey();
		}
	}
}
