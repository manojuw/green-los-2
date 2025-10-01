package com.mixo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mixo.model.Permission;
import com.mixo.model.Role;
import com.mixo.model.User;
import com.mixo.repository.PermissionRepository;
import com.mixo.repository.RoleRepository;
import com.mixo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class MixoApplication {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(MixoApplication.class, args);
	}

	@Bean
	CommandLineRunner onStartup() {
		return args -> {
			if (userRepository.count() == 0) {
				Permission readPermission = new Permission("PERMISSION_USER_VIEW");
				Permission writePermission = new Permission("PERMISSION_PERMISSION_VIEW");
				Permission rolePermission = new Permission("PERMISSION_ROLE_VIEW");

				Role adminRole = new Role("ADMIN");
				adminRole.getPermissions().add(readPermission);
				adminRole.getPermissions().add(writePermission);
				adminRole.getPermissions().add(rolePermission);

				User admin = new User("admin", passwordEncoder.encode("Admin@123"), true);
				admin.getRoles().add(adminRole);

				permissionRepository.saveAll(List.of(readPermission, writePermission, rolePermission));
				roleRepository.saveAll(List.of(adminRole));
				userRepository.saveAll(List.of(admin));
				log.info("Admin user created.");
			} else {
				log.info("Database already contains users. Skipping user creation.");
			}
		};
	}

}
