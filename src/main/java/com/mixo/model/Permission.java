package com.mixo.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToMany(mappedBy = "permissions")
	private Set<Role> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Permission(String name) {
		super();
		this.name = name;
	}

	public Permission(Long id, String name, Set<Role> roles) {
		super();
		this.id = id;
		this.name = name;
		this.roles = roles;
	}

	public Permission(String name, Set<Role> roles) {
		super();
		this.name = name;
		this.roles = roles;
	}

	public Permission() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
