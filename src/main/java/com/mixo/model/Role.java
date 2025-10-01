package com.mixo.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@ManyToMany(mappedBy = "roles")
	private Set<User> users;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "roles_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();

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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Role(String name) {
		super();
		this.name = name;
	}

	public Role(String name, Set<User> users, Set<Permission> permissions) {
		super();
		this.name = name;
		this.users = users;
		this.permissions = permissions;
	}

	public Role(Long id, String name, Set<User> users, Set<Permission> permissions) {
		super();
		this.id = id;
		this.name = name;
		this.users = users;
		this.permissions = permissions;
	}

	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
}
