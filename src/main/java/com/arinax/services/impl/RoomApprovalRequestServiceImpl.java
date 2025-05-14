package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.Room;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.RoomApprovalRequestDto;
import com.arinax.repositories.RoomApprovalRequestRepo;
import com.arinax.repositories.RoomRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.RoomApprovalRequestService;


@Service
public class RoomApprovalRequestServiceImpl implements RoomApprovalRequestService{

	@Autowired
	RoomRepo roomRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	RoomApprovalRequestRepo roomApprovalRequestRepo;
	
	@Autowired
	UserTransactionRepo userTransactionRepo;
	 @Autowired
	 private RoomApprovalRequestRepo roomApprovalRepo;
	
	@Override
	public RoomApprovalRequestDto createRoomApproval(RoomApprovalRequestDto roomApprovalRequestDto, Integer roomId, Integer userId) {
	    Room room = roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "Room ID", roomId));

	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

	    if (user.getBalance() < room.getEntryFee()) {
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

	@Override
	  public List<RoomApprovalRequestDto> getAllApprovalsByRoomId(Integer roomId) {
	        Room room = roomRepo.findById(roomId)
	                .orElseThrow(() -> new ResourceNotFoundException("Room", "Room ID", roomId));

	        List<RoomApprovalRequest> approvals = roomApprovalRepo.findByRoom(room);
	        
	        return approvals.stream()
	                .map(request -> modelMapper.map(request, RoomApprovalRequestDto.class))
	                .collect(Collectors.toList());
	    }
	
	@Override
	public RoomApprovalRequestDto approveRoomRequest(Integer roomAppId, Integer userId) {
	    RoomApprovalRequest roomapp = roomApprovalRepo.findById(roomAppId)
	            .orElseThrow(() -> new ResourceNotFoundException("RoomApprovalRequest", "RoomApp ID", roomAppId));

	    User currentuser = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

	    if (roomapp.getRoom().getUser().getId() != currentuser.getId()) {
	        throw new ApiException("You are not authorized to approve this request");
	    }


	    if (roomapp.getStatus() != RoomApprovalRequest.ApprovedStatus.PENDING) {
	        throw new ApiException("Request has already been approved or rejected");
	    }

	    if (roomapp.getUser().getBalance() < roomapp.getRoom().getEntryFee()) {
	        throw new ApiException("Insufficient balance");
	    }

	    roomapp.getRoom().setStatus(Room.RoomStatus.PLAYER_APPROVED);
	    roomRepo.save(roomapp.getRoom()); 
	    User user = roomapp.getUser();
	    user.setBalance(user.getBalance() - roomapp.getRoom().getEntryFee());
	    
	    UserTransaction txn = new UserTransaction();
        txn.setUser(user);
        txn.setAmount(-roomapp.getRoom().getEntryFee()); // Positive means credited
        txn.setType("DEBITED");
        txn.setReason("coin is reduce from Your account");
        txn.setDateTime(LocalDateTime.now());
        userTransactionRepo.save(txn); // Save transaction
	    
	    userRepo.save(user); // Save the updated user balance

	    roomapp.setStatus(RoomApprovalRequest.ApprovedStatus.CREATOR_APPROVED);
	    RoomApprovalRequest updatedRequest = roomApprovalRepo.save(roomapp); // Save the updated request

	    return modelMapper.map(updatedRequest, RoomApprovalRequestDto.class);
	}



	
}
