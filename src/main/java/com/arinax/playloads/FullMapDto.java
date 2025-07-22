package com.arinax.playloads;

import java.time.LocalDateTime;

import com.arinax.entities.FullMap.FullMapStatus;

import lombok.Data;
@Data
public class FullMapDto {


	private Integer fullmap_Id;

	
	private String title;

	private String content;

	private String imageName1;
	private String imageName2;


	private LocalDateTime addedDate;


	private LocalDateTime startTime;
	
	private Double entryFee;
	
	
	    private FullMapStatus status;
	    
//	    public enum PostStatus {
//	        PRIVATE, REJECTED,PLAYER_APPROVED, PENDING
//	    }
//	
	
	    private GameDto game;

	    private GameModeDto gameMode;
	 
	    private UserDto user;
}
