package com.arinax.playloads;

import java.time.LocalDateTime;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadCoinDto {

    private Integer loadCoinId;

	private String  screenshort;
	
	private String paymentMethod;
	
	private LocalDateTime requestAt= LocalDateTime.now();
	

	private UserDto user;

	    private LoadCoinStatus status;
	    
	    public enum LoadCoinStatus {
	         REJECTED,APPROVED, PENDING
	    }
	
}
