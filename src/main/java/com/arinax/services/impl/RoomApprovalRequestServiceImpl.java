package com.arinax.services.impl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.arinax.entities.Room;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.User;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.RoomApprovalRequestDto;
import com.arinax.repositories.RoomApprovalRequestRepo;
import com.arinax.repositories.RoomRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.services.RoomApprovalRequestService;



public class RoomApprovalRequestServiceImpl implements RoomApprovalRequestService{

	@Autowired
	RoomRepo roomRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	ModelMapper modelMapper;
	
	RoomApprovalRequestRepo roomApprovalRequestRepo;
	
	@Override
	public RoomApprovalRequestDto createRoomApproval(RoomApprovalRequestDto roomApprovalRequestDto, Integer roomId, Integer userId) {
	    Room room = roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "Room ID", roomId));

	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

	    if (user.getBalance() < 21.0) {
	        throw new ApiException("Insufficient Balance");
	    }

	    if (room.getStatus() == Room.RoomStatus.DISAPPEAR ||room.getStatus()==Room.RoomStatus.PLAYER_APPROVED||
	        room.getStatus() == Room.RoomStatus.PRIVATE) {
	        throw new ApiException("This Room has already been Approved/Rejected.");
	    }

	    RoomApprovalRequest ab = new RoomApprovalRequest();
	    ab.setStatus(RoomApprovalRequest.ApprovedStatus.PENDING);
	    ab.setUser(user);
	    ab.setRoom(room);
	    ab.setRequestAt(LocalDateTime.now());

	   // room.setStatus(Room.RoomStatus.PLAYER_APPROVED);// creator le accept garya paxi matra garne

	    RoomApprovalRequest saved = roomApprovalRequestRepo.save(ab);
	    return modelMapper.map(saved, RoomApprovalRequestDto.class);
	}


}
