package com.mixo.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.model.Permission;
import com.mixo.model.Role;
import com.mixo.repository.PermissionRepository;
import com.mixo.repository.RoleRepository;
import com.mixo.service.RoleService;

@Service
public class RoleServiceimpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public List<Role> findAllPermissions() {

		return roleRepository.findAll();
	}

	@Override
	public String save(String roleName, String permissionName) {
		String message = "";
		Role role = roleRepository.findByName(roleName);
		Permission permission = permissionRepository.findByName(permissionName);

		if (role == null || permission == null) {
			message = "Invalid role or permission selected.";
			return message;
		}

		// Check for duplicates
		if (role.getPermissions().contains(permission)) {
			message = "Permission is already assigned to this role.";
			return message;
		}

		// Add the permission to the role
		role.getPermissions().add(permission);
		roleRepository.save(role);
		message = "Permission assigned successfully.";
		return message;

	}

	@Override
	public String saveRole(Role role) {

		String message = "";
		Role roleCheck = roleRepository.findByName(role.getName().toUpperCase());

		if (roleCheck != null) {
			message = "Role already exists.";
			return message;
		}

		role.setName(role.getName().toUpperCase());
		roleRepository.save(role);
		message = "Role added successfully.";
		return message;
	}

}
