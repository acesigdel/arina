package com.arinax.entities;

import java.time.LocalDateTime;

import com.arinax.entities.RoomApprovalRequest.ApprovedStatus;

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
public class TurnmentApproval {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer postAppId;
	
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
	    @JoinColumn(name = "post_Id") // Fixed column name
	  //@JsonBackReference
	    private Post post;
}
