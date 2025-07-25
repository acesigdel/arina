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
public class FullMapApproval {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer fullmapAppId;
	
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
	    @JoinColumn(name = "fullmap_Id") // Fixed column name
	  //@JsonBackReference
	    private FullMap fullMap;
}
