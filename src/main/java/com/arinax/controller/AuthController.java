package com.arinax.controller;

import java.security.Principal;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.entities.User;
import com.arinax.exceptions.ApiException;
import com.arinax.playloads.JwtAuthRequest;
import com.arinax.playloads.JwtAuthResponse;
import com.arinax.playloads.UserDto;
import com.arinax.playloads.VerificationDto;
import com.arinax.repositories.UserRepo;
import com.arinax.security.GoogleTokenVerifier;
import com.arinax.security.JwtTokenHelper;
import com.arinax.services.UserService;
import com.arinax.services.impl.VerificationService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

	@Autowired
    private VerificationService verificationService;

	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;
	
	@PostMapping("/request-otp")
	public String requestOtp(@RequestBody VerificationDto verificationDto) {
	    String email = verificationDto.getEmail(); // dto बाट email निकाल्ने
	    verificationService.getEmail(email);
	    return "OTP sent to your email";
	}

	@PostMapping("/google-login")
	public ResponseEntity<JwtAuthResponse> googleLogin(@RequestBody Map<String, String> request) {
	    String idToken = request.get("idToken");
	    GoogleIdToken.Payload payload = GoogleTokenVerifier.verify(idToken);

	    if (payload == null) {
	        throw new ApiException("Invalid Google token");
	    }

	    String email = payload.getEmail();
	    String name = (String) payload.get("name");

	    User user = userRepo.findByEmail(email).orElseGet(() -> {
	        User newUser = new User();
	        newUser.setEmail(email);
	        newUser.setName(name);
	        newUser.setPassword("231998"); // optional
	        return userRepo.save(newUser);
	    });

	    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
	    String token = jwtTokenHelper.generateToken(userDetails);

	    JwtAuthResponse response = new JwtAuthResponse();
	    response.setToken(token);
	    response.setUser(this.mapper.map(user, UserDto.class));
	    return ResponseEntity.ok(response);
	}

	 
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest request) throws Exception {
		this.authenticate(request.getUsername(), request.getPassword());
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
		String token = this.jwtTokenHelper.generateToken(userDetails);

		JwtAuthResponse response = new JwtAuthResponse();
		response.setToken(token);
		response.setUser(this.mapper.map((User) userDetails, UserDto.class));
		return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
	}

	private void authenticate(String username, String password) throws Exception {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);

		try {

			this.authenticationManager.authenticate(authenticationToken);

		} catch (BadCredentialsException e) {
			System.out.println("Invalid Detials !!");
			throw new ApiException("Invalid username or password !!");
		}

	}

	// register new user api

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
		UserDto registeredUser = this.userService.registerNewUser(userDto);
		return new ResponseEntity<UserDto>(registeredUser, HttpStatus.CREATED);
	}

	// get loggedin user data
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper mapper;

	@GetMapping("/current-user/")
	public ResponseEntity<UserDto> getUser(Principal principal) {
		User user = this.userRepo.findByEmail(principal.getName()).get();
		return new ResponseEntity<UserDto>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
	}

	 
	   }


