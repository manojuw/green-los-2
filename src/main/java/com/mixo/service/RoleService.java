package com.mixo.service;

import java.util.List;

import com.mixo.model.Role;

public interface RoleService {

	List<Role> findAllPermissions();


	String save(String roleName, String permissionName);


	String saveRole(Role role);

}
