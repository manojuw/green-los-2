package com.mixo.service;

import java.util.List;

import com.mixo.model.Permission;

public interface PermissionService {
	public String save(Permission permission);

	public List<Permission> findAllPermissions();

}
