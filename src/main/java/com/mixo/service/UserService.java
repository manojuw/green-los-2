package com.mixo.service;

import java.util.List;

import com.mixo.dto.UserDTO;
import com.mixo.dto.UserListDto;

public interface UserService {

	String saveUser(UserDTO userDTO);

	List<UserListDto> getAllUsers(String userName);

}
