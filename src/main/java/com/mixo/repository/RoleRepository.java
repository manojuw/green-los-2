package com.mixo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Role findByName(String name);

}
