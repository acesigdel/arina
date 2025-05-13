package com.arinax.entities;

import java.time.LocalDateTime;

import com.arinax.entities.Post.PostStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class LoadCoin {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer loadCoinId;

	private String  screenshort;
	
	private LocalDateTime requestAt= LocalDateTime.now();
	
	private String paymentMethod;
	@ManyToOne
	private User user;
	
	 @Enumerated(EnumType.STRING)
	    private LoadCoinStatus status;
	    
	    public enum LoadCoinStatus {
	         REJECTED,APPROVED, PENDING
	    }
	    
	    private String rejectMsg;
	    
	boolean topup=false;
	 
	
	
}
