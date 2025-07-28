package com.arinax.playloads;



import lombok.Data;

@Data
public class OtpRequestDto {
	  private String emailOrMobile;
	    private String otp;
}
