package com.mixo.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.model.ApiKeyManager;
import com.mixo.repository.ApiKeyManagerRepository;
import com.mixo.service.ApiKeyManagerService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiKeyManagerServiceImpl implements ApiKeyManagerService {
	@Autowired
	ApiKeyManagerRepository apiKeyManagerRepository;

	@Override
	public ApiKeyManager getApiKeyManager(String uid) {
		Optional<ApiKeyManager> apiKey = apiKeyManagerRepository.findByUid(uid);

		if (apiKey.isPresent()) {
			return apiKey.get();
		}
		return null;
	}

	@Override
	public String saveKey(ApiKeyManager apiKeyManager, String userName) {
		Optional<ApiKeyManager> apiKey = apiKeyManagerRepository.findByUid(apiKeyManager.getUid());

		if (apiKey.isPresent()) {
			ApiKeyManager key = apiKey.get();
			key.setApiKey(apiKeyManager.getApiKey());
			apiKeyManagerRepository.save(key);
			return "ApiKey Updated Successfully";
		}

		ApiKeyManager key = new ApiKeyManager();
		key.setUid(apiKeyManager.getUid());
		key.setApiKey(apiKeyManager.getApiKey());
		apiKeyManagerRepository.save(key);

		return "ApiKey Saved Successfully";
	}

}
