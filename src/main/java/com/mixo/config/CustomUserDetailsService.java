package com.mixo.config;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mixo.model.Role;
import com.mixo.model.User;
import com.mixo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (!user.isActive()) {
			throw new RuntimeException("User is not active");
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
		return roles.stream().flatMap(role -> role.getPermissions().stream())
				.map(permission -> new SimpleGrantedAuthority(permission.getName())).collect(Collectors.toList());
	}
}
