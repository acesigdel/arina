package com.arinax.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Entity
@Data
public class RoomApprovalRequest {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer roomAppId;
	
	private LocalDateTime requestAt;
	
	 @Enumerated(EnumType.STRING)
	    private ApprovedStatus status;
	    
	    public enum ApprovedStatus {
	        PENDING,CREATOR_APPROVED, REJECTED
	    }
	    
	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private User user;
	  
	  @ManyToOne
	    @JoinColumn(name = "room_Id") // Fixed column name
	  //@JsonBackReference
	    private Room room;
}
