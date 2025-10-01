package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Permission findByName(String name);

}
