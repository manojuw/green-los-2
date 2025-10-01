package com.mixo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mixo.model.ApiKeyManager;
import com.mixo.service.ApiKeyManagerService;
import com.mixo.service.NbfcService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class KeyContoller {

	@Autowired
	ApiKeyManagerService apiKeyManagerService;

	@Autowired
	NbfcService nbfcService;

	@RequestMapping("/key")
	public String addNewProduct(Model model) {
		model.addAttribute("key", new ApiKeyManager());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "key";
	}

	@PostMapping("/addKey")
	public String addKey(@ModelAttribute("key") ApiKeyManager apiKeyManager, BindingResult result, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		if (result.hasErrors()) {
			model.addAttribute("key", apiKeyManager);
			model.addAttribute("message", result.getAllErrors());

			model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
			return "key";
		}

		String message = apiKeyManagerService.saveKey(apiKeyManager, userName); // Save product through the service
																				// layer
		model.addAttribute("message", message);
		model.addAttribute("key", apiKeyManager);
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "key"; // Redirect to the user list page after successful save
	}

	@GetMapping("/getKeyValue")
	public ResponseEntity<Map<String, String>> getKeyValue(@RequestParam String partnerName) {
		// Simulate dynamic response based on partnerName

		Map<String, String> response = new HashMap<>();
		ApiKeyManager apiKeyManager = apiKeyManagerService.getApiKeyManager(partnerName);
		if (apiKeyManager == null) {
			response.put("key", "");
			response.put("placeholder", "Enter Key ..");
		} else {
			response.put("key", apiKeyManager.getApiKey());
			response.put("placeholder", "Enter Key ..");
		}
		return ResponseEntity.ok(response);
	}

}
