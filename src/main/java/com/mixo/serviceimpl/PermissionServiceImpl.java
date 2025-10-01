package com.mixo.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.model.Permission;
import com.mixo.repository.PermissionRepository;
import com.mixo.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public String save(Permission permission) {

		Permission existingPermission = permissionRepository.findByName(permission.getName());

		if (existingPermission != null) {
			return "Permission already exists";
		}
		permissionRepository.save(permission);

		return "Permission saved successfully";

	}

	@Override
	public List<Permission> findAllPermissions() {
		return permissionRepository.findAll();
	}

}
