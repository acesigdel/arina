package com.arinax.services;

import java.util.List;

import com.arinax.playloads.UserDto;


public interface UserService {

UserDto registerNewUser(UserDto user);
	
	
	UserDto createUser(UserDto user);

	UserDto updateUser(UserDto user, Integer userId);

	UserDto getUserById(Integer userId);

	List<UserDto> getAllUsers();

	void deleteUser(Integer userId);


	UserDto BalanceUpdate(UserDto userDto, Integer userId);


	UserDto updateDeviceToken(UserDto userDto, Integer userId);


	void addRoleToUser(String email, String roleName);


	UserDto getUserByEmail(String email);


	List<UserDto> getUsersByRole(String roleName);


	
	
	

}
