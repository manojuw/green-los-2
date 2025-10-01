package com.mixo.service;

import com.mixo.model.ApiKeyManager;

public interface ApiKeyManagerService {

	ApiKeyManager getApiKeyManager(String uid);

	String saveKey(ApiKeyManager apiKeyManager, String userName);

}
