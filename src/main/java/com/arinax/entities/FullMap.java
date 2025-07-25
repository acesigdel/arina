package com.arinax.entities;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
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
public class FullMap {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name="post_id, nullable=false")
	private Integer  fullmapId;

	@Column(name = "fullmap_title", length = 100, nullable = false)
	private String title;

	@Column(length = 1000000000)
	private String content;

	private String imageName1;
	private String imageName2;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime addedDate;

	@Column(name = "event_start", nullable = true)
	private LocalDateTime startTime;
	
	private Double entryFee;
	
	 @Enumerated(EnumType.STRING)
	    private FullMapStatus status;
	    
	    public enum FullMapStatus {
	        PRIVATE, REJECTED,PLAYER_APPROVED, PENDING
	    }
	
	    @ManyToOne
	    @JoinColumn(name = "game_id")
	    private Game game;

	    @ManyToOne
	    @JoinColumn(name = "mode_id", nullable = false)
	    private GameMode gameMode;
	 
	@ManyToOne
	private User user;
	
}
