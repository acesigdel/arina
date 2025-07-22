package com.arinax.services.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.arinax.exceptions.ApiException;
import com.arinax.playloads.VerificationDto;
import com.arinax.repositories.UserRepo;
@Service
public class VerificationService {

	@Autowired
	UserRepo userRepo;
	@Autowired
    private JavaMailSender mailSender;
	 private Map<String, VerificationDto> otpStore = new HashMap<>(100, 0.75f);

	 public void getEmail(String email) {
		 
		 if (userRepo.findByEmail(email).isPresent()) {
		        throw new ApiException("User already registered with this email.");
		    }
		    String otp = generateOtp();
		    Instant timestamp = Instant.now();

		    VerificationDto dto = new VerificationDto();
		    dto.setEmail(email);
		    dto.setOtp(otp);
		    dto.setTimestamp(timestamp);

		    // HashMap मा save गर्ने
		    otpStore.put(email, dto);

		    // Email पठाउने
		    sendOtpEmail(email, "OTP Verification", "Your OTP is: " + otp);
		}


	  private void sendOtpEmail(String to, String subject, String message) {
	        SimpleMailMessage mailMessage = new SimpleMailMessage();
	        mailMessage.setTo(to);
	        mailMessage.setSubject(subject);
	        mailMessage.setText(message);
	        mailMessage.setFrom("aoneitinnovation@gmail.com");
	        mailSender.send(mailMessage);
	    }
	    
	  private String generateOtp() {
	        Random random = new Random();
	        int otp = 100000 + random.nextInt(900000);
	        return String.valueOf(otp);
	    }
	  public VerificationDto getOtpDetails(String email) {
		    return otpStore.get(email);
		}
	  public void removeOtp(String email) {
		    otpStore.remove(email);
		}



	  @Scheduled(cron = "0 0 0 * * *") // every day at 12:00 AM
	  public void removeExpiredOtpsAtMidnight() {
	      otpStore.clear();
	      System.out.println("OTP Store cleared at midnight");
	  }

}
