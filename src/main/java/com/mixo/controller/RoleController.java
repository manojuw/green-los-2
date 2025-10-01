package com.mixo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mixo.model.Permission;
import com.mixo.model.Role;
import com.mixo.service.PermissionService;
import com.mixo.service.RoleService;

@Controller
public class RoleController {

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@RequestMapping("/role")
	public String permission(Model model) {

		List<Role> role = roleService.findAllPermissions();
		List<Permission> permission = permissionService.findAllPermissions();

		model.addAttribute("roles", role);
		model.addAttribute("permissions", permission);

		return "role";
	}

	@PostMapping("/addRole")
	public String addRole(@ModelAttribute("role") Role role, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("role", role);
			model.addAttribute("message", result.getAllErrors());
			List<Role> roles = roleService.findAllPermissions();
			List<Permission> permission = permissionService.findAllPermissions();

			model.addAttribute("roles", roles);
			model.addAttribute("permissions", permission);
			return "role";
		}

		List<Role> roles = roleService.findAllPermissions();
		List<Permission> permission = permissionService.findAllPermissions();

		model.addAttribute("roles", roles);
		model.addAttribute("permissions", permission);

		String message = roleService.saveRole(role); // Save user through the service layer
		model.addAttribute("message", message);
		return "role"; // Redirect to the user list page after successful save
	}

	@PostMapping("/addRolePermission")
	public String addRolePermission(@RequestParam("role") String roleName,
			@RequestParam("permission") String permissionName, Model model) {

		// Check if the role and permission are valid
		if (roleName == null || roleName.isEmpty() || permissionName == null || permissionName.isEmpty()) {
			model.addAttribute("messgae", "Invalid role or permission selected.");
			return "role"; // Return to the same form view with an error message
		}

		// Fetch the Role and Permission entities

		String message = roleService.save(roleName, permissionName); // Save the updated role with the new permission

		// Add success message
		model.addAttribute("message", message);

		// Redirect or return to the form view

		List<Role> role = roleService.findAllPermissions();
		List<Permission> permission = permissionService.findAllPermissions();

		model.addAttribute("roles", role);
		model.addAttribute("permissions", permission);
		return "role"; // Redirect to the role form
	}

}
