package com.mixo.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mixo.dto.NbfcDto;
import com.mixo.dto.NbfcRequestDto;
import com.mixo.model.Nbfc;
import com.mixo.model.Role;
import com.mixo.model.User;
import com.mixo.repository.NbfcRepository;
import com.mixo.repository.RoleRepository;
import com.mixo.repository.UserRepository;
import com.mixo.service.AwsS3Service;
import com.mixo.service.NbfcService;
import com.mixo.utils.AlphaNumIdGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NbfcServiceImpl implements NbfcService {

	private final String UPLOAD_DIR = "opt/uploads/";

	@Autowired
	UserRepository userRepository;

	@Autowired
	NbfcRepository nbfcRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	AwsS3Service awsS3Service;

	@Override
	public String saveNbfc(NbfcDto nbfcDto) {

		try {

			Optional<User> user = userRepository.findByUsername(nbfcDto.getUserName());
			if (user.isPresent()) {
				return "UserName already exists";

			}
			Optional<Nbfc> nbfc = nbfcRepository.findByBrandName(nbfcDto.getBrandName());
			if (nbfc.isPresent()) {
				return "BrandName already exists";
			}
			User userObj = new User();
			userObj.setUsername(nbfcDto.getUserName());
			userObj.setPassword(nbfcDto.getPassword());
			userObj.setBusinessName(nbfcDto.getCoLenderName());
			userObj.setEmailId(nbfcDto.getOrganisationEmail());
			userObj.setMobile(nbfcDto.getAuthorisedPersonMobile());
			userObj.setActive(true);
			userObj.setUid(AlphaNumIdGenerator.generateId(20));

			Role adminRole = roleRepository.findByName("NBFC");

			if (adminRole == null) {
				adminRole = new Role();
				adminRole.setName("NBFC");
				roleRepository.save(adminRole);
			}
			userObj.getRoles().add(adminRole);
			userRepository.save(userObj);

			Nbfc nbfcObj = new Nbfc();
			BeanUtils.copyProperties(nbfcDto, nbfcObj);
			nbfcObj.setUid(userObj.getUid());
			nbfcRepository.save(nbfcObj);

			return "NBFC added successfully";
		} catch (Exception e) {
			log.error(e.getMessage());

			Optional<User> user = userRepository.findByUsername(nbfcDto.getUserName());
			User userObj = user.get();
			userRepository.deleteById(userObj.getId());
			return "Something went wrong";

		}
	}

	private String saveFile(MultipartFile file, String prefix) throws IOException {
		if (file != null && !file.isEmpty()) {
			String fileName = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR + fileName);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, file.getBytes());
			return filePath.toString();
		}
		return null;
	}

	@Override
	public List<NbfcDto> getAllNbfc(String userName) {

		List<NbfcDto> users = new ArrayList<>();
		List<Nbfc> nbfcList = nbfcRepository.findAll();
		if (nbfcList.isEmpty()) {
			return users;
		}
		for (Nbfc nbfc : nbfcList) {
			NbfcDto nbfcDto = new NbfcDto();
			BeanUtils.copyProperties(nbfc, nbfcDto);
			users.add(nbfcDto);
		}
		return users;

	}

	@Override
	public List<NbfcRequestDto> getAllNbfcRequestDto(Authentication auth) {
		// Check if the user has the "viewNbfc" permission
		List<NbfcRequestDto> users = new ArrayList<>();
		if (auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).noneMatch("VIEW_NBFC"::equals)) {
			return users;
		}

		List<Nbfc> nbfcList = nbfcRepository.findAll();
		if (nbfcList.isEmpty()) {
			return users;
		}
		for (Nbfc nbfc : nbfcList) {
			NbfcRequestDto nbfcDto = new NbfcRequestDto();
			BeanUtils.copyProperties(nbfc, nbfcDto);
			users.add(nbfcDto);
		}
		return users;
	}

	@Override
	public Optional<Nbfc> getNbfcByUid(String uid) {
		return nbfcRepository.findByUid(uid);
	}

	@Override
	public String updateNbfc(NbfcDto nbfcDto) {
		Optional<Nbfc> nbfc = nbfcRepository.findByUid(nbfcDto.getUid());
		if (nbfc.isPresent()) {
			Nbfc nbfcObj = nbfc.get();
			BeanUtils.copyProperties(nbfcDto, nbfcObj);
			nbfcRepository.save(nbfcObj);
			return "NBFC updated successfully";
		}

		return "Something went wrong";
	}

}
