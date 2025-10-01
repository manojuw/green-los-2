package com.mixo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mixo.model.Permission;
import com.mixo.service.PermissionService;

@Controller
public class PermissionController {

	@Autowired
	PermissionService permissionService;

	@RequestMapping("/permission")
	public String permission(Model model) {

		List<Permission> permissions = permissionService.findAllPermissions();
		model.addAttribute("permissions", permissions);
		return "permission";
	}

	@PostMapping("/addPermission")
	public String addPermission(@ModelAttribute("permissionName") String permissionName, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("error", "Validation errors occurred");
			return "permission";
		}

		Permission permission = new Permission();
		permission.setName(permissionName);

		String message = permissionService.save(permission);
		model.addAttribute("message", message);

		List<Permission> permissions = permissionService.findAllPermissions();
		model.addAttribute("permissions", permissions);
		return "permission";
	}
	
	

}
