package com.mixo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.service.ApiKeyService;

@Controller
public class DashBoardController {
	
	
	@Autowired
	ApiKeyService apiKeyService;
	
	
	@RequestMapping("/dashboard")
	public String dashboard(Model model) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		String role = auth.getAuthorities().toString();
		String permission = auth.getAuthorities().toString();
		System.out.println("Role "+role);
		System.out.println("Permission "+permission);
		System.out.println("User Name "+userName);
		model.addAttribute("apiKey", apiKeyService.getApiKey());
		
		return "dashboard";
	}

	


}
