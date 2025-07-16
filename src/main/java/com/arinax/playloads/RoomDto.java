package com.arinax.playloads;

import java.time.LocalDateTime;

import com.arinax.entities.Game;
import com.arinax.entities.User;
import com.arinax.entities.Room.RoomStatus;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {
	
	private Integer roomId;
	
	private String content;

	private LocalDateTime addedDate;

	//private String startTime;
	
	private Double entryFee;
	
	private Double wining;
	private String gameType; // e.g. "1v1", "2v2", ..., "8v8"

	private RoomStatus status;
	 public enum RoomStatus {
	        PENDING,PLAYER_APPROVED, DISAPPEAR, PRIVATE
	    }
	private Integer inventory=0;
	private String creator_SS;
	private String player_SS;
	private GameDto game;
	 
	private UserDto user;
	
}
