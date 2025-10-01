package com.mixo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mixo.dto.UserDTO;
import com.mixo.dto.UserListDto;
import com.mixo.model.Role;
import com.mixo.model.User;
import com.mixo.repository.RoleRepository;
import com.mixo.repository.UserRepository;
import com.mixo.service.UserService;
import com.mixo.utils.AlphaNumIdGenerator;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public String saveUser(UserDTO userDTO) {

		Optional<User> user = userRepository.findByUsername(userDTO.getUserName());

		if (user.isPresent()) {
			return "User already exists";
		}

		user = Optional.of(new User());
		user.get().setUsername(userDTO.getUserName());
		user.get().setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.get().setParentUid(userDTO.getResellerId());

		user.get().setBusinessName(userDTO.getBusinessName());
		user.get().setEmailId(userDTO.getEmailId());
		user.get().setMobile(userDTO.getMobile());
		user.get().setActive(true);
		user.get().setUid(AlphaNumIdGenerator.generateId(20));

		Role adminRole = roleRepository.findByName(userDTO.getRole());

		if (adminRole == null) {
			adminRole = new Role();
			adminRole.setName(userDTO.getRole());
			roleRepository.save(adminRole);
		}
		user.get().getRoles().add(adminRole);

		userRepository.save(user.get());
		return "User added successfully";
	}

	@Override
	public List<UserListDto> getAllUsers(String userName) {
		List<UserListDto> userDTOs = new ArrayList<>();
		// Find the user by username
		User user = userRepository.findByUsername(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// List to store UserDTOs
		List<UserListDto> users = new ArrayList<>();

		// Check if the user's role is ADMIN
		if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
			// Fetch all users if the current user is an admin
			List<User> userList = userRepository.findAll();

			// Convert all users to UserListDto
			users = userList.stream().map(this::mapToUserListDto).collect(Collectors.toList());
		}

		return users.isEmpty() ? userDTOs : users;
	}

	private UserListDto mapToUserListDto(User user) {
		UserListDto userDTO = new UserListDto();
		userDTO.setUsername(user.getUsername());
		userDTO.setUid(user.getUid());
		userDTO.setBusinessName(user.getBusinessName());
		userDTO.setEmailId(user.getEmailId());
		userDTO.setMobile(user.getMobile());
		userDTO.setRole(user.getRoles().stream().findFirst().map(Role::getName).orElse(null)); // Handle null roles
																								// safely
		userDTO.setParentUid(user.getParentUid());
		return userDTO;
	}
}
