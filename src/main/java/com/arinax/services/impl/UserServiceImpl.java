package com.arinax.services.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.arinax.config.AppConstants;
import com.arinax.entities.Role;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.UserDto;
import com.arinax.playloads.VerificationDto;
import com.arinax.repositories.RoleRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.NotificationService;
import com.arinax.services.UserService;



@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserTransactionRepo userTransactionRepo;
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private VerificationService verificationService;

	@Autowired
	private NotificationService notificationService;

	private static final long OTP_VALID_DURATION = 5 * 60; // 5 min in seconds

	
	@Override
	public UserDto registerNewUser(UserDto userDto) {

		User user = this.modelMapper.map(userDto, User.class);

		// OTP Verify
	    VerificationDto verification = verificationService.getOtpDetails(userDto.getEmail());

	    if (verification == null || !verification.getOtp().equals(userDto.getOtp())) {
	        throw new ApiException("Invalid OTP or email!");
	    }
	    
	    // Time validation
	    Instant otpGeneratedTime = verification.getTimestamp();
	    Instant now = Instant.now();

	    long elapsedSeconds = Duration.between(otpGeneratedTime, now).getSeconds();

	    if (elapsedSeconds > OTP_VALID_DURATION) {
	        verificationService.removeOtp(userDto.getEmail()); // OTP expire भैसकेपछि हटाउने
	        throw new ApiException("OTP expired! Please request a new one.");
	    }
	    
	    // OTP सही भयो र time भित्र verify भयो भने हटाउने
	    verificationService.removeOtp(userDto.getEmail());

		// encoded the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// roles
		Role role = this.roleRepo.findById(AppConstants.NORMAL_USER)
				.orElseThrow(() -> new RuntimeException("Role not found with id: " + AppConstants.NORMAL_USER));

		user.setRoles(new HashSet<>());          // initialize
		user.getRoles().add(role);               // one role add (NORMAL_USER)
		String generatedUsername = generateUniqueUsername(user.getEmail(), userDto.getMobileNo());
		
        user.setURemark(generatedUsername);
		user.setMobileNo(userDto.getMobileNo());
		User newUser = this.userRepo.save(user);
		String welcomeMessage = String.format("Welcome, %s! We're excited to have you on our ArinaX. enjoy the journey ahead! "
        		+ "Thank you for choosing us, Arinax", user.getName());
       // sendmsg.sendMessage(user.getMobileNo(), welcomeMessage); // Assuming notificationService sends SMS

		
		 notificationService.createNotification(newUser.getId(), welcomeMessage);
		return this.modelMapper.map(newUser, UserDto.class);
	}

	public String generateUniqueUsername(String email, String mobileNo) {
	    String emailPrefix = email.split("@")[0]; // e.g., "john.doe"
	    String mobileSuffix = mobileNo.substring(mobileNo.length() - 4); // e.g., "2334"9816032025
	    String baseUsername = emailPrefix.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + mobileSuffix;

	    String finalUsername = baseUsername;
	    int counter = 1;

	    while (userRepo.existsByURemark(finalUsername)) {
	        finalUsername = baseUsername + counter;
	        counter++;
	    }

	    return finalUsername;
	}

	
	
	@Override
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {
	    User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

	    user.setName(userDto.getName());
	    user.setEmail(userDto.getEmail());
	    user.setFuId(userDto.getFuId());
	    user.setPuId(userDto.getPuId());

	    // Encode password before update
	    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
	        user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
	    }

	    User updatedUser = this.userRepo.save(user);
	    return this.userToDto(updatedUser);
	}

	
	@Override
	public UserDto BalanceUpdate(UserDto userDto, Integer userId) {
	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

	    double incomingBalance = userDto.getBalance();
	    if (incomingBalance == 0) return userToDto(user);

	    user.setBalance(user.getBalance() + incomingBalance);
	    
	    UserTransaction txn = new UserTransaction();
        txn.setUser(user);
        txn.setAmount(+incomingBalance); // Positive means credited
        txn.setType("CREDITED");
        txn.setReason("coin is added in Your account");
        txn.setDateTime(LocalDateTime.now());
        userTransactionRepo.save(txn); // Save transaction
        
	    return userToDto(userRepo.save(user));
	}

		
	

	@Override
	public UserDto getUserById(Integer userId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

		return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {

		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());

		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		this.userRepo.delete(user);

	}

	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		return user;
	}

	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}


	
}

