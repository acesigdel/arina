package com.arinax.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.entities.RoomApprovalRequest.ApprovedStatus;
import com.arinax.playloads.RoomApprovalRequestDto;

import com.arinax.services.RoomApprovalRequestService;

@RestController
@RequestMapping("/api/v1/roomApp")
public class RoomApprovalRequestController {

	
	 @Autowired
	    private RoomApprovalRequestService roomApprovalRequestService;
	

	    @PostMapping("/room/{roomId}/user/{userId}")
	    public ResponseEntity<RoomApprovalRequestDto> createRoomApproval(
	            @RequestBody RoomApprovalRequestDto roomApprovalRequestDto,
	            @PathVariable Integer roomId,
	            @PathVariable Integer userId) {
	        
	        RoomApprovalRequestDto createdRequest = roomApprovalRequestService.createRoomApproval(
	                roomApprovalRequestDto, roomId, userId);
	        
	        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
	    }
	    
	    //get all list of approvals for room
//	    @GetMapping("/room/{roomId}/approvals")
//	    public ResponseEntity<List<RoomApprovalRequestDto>> getAllApprovalsByRoomId(@PathVariable Integer roomId) {
//	        List<RoomApprovalRequestDto> approvals = roomApprovalRequestService.getAllApprovalsByRoomId(roomId);
//	        return new ResponseEntity<>(approvals, HttpStatus.OK);
//	    }
	    @GetMapping("/room/{roomId}/approvals")
	    public ResponseEntity<List<RoomApprovalRequestDto>> getApprovalsByRoomIdAndStatus(
	            @PathVariable Integer roomId,
	            @RequestParam(required = false) ApprovedStatus status) {

	        List<RoomApprovalRequestDto> approvals = roomApprovalRequestService
	            .getApprovalsByRoomIdAndStatus(roomId, status);
	            
	        return new ResponseEntity<>(approvals, HttpStatus.OK);
	    }


	    @PostMapping("/approve/{roomAppId}/user/{userId}")
	    public ResponseEntity<RoomApprovalRequestDto> approveRoomRequest(
	            @PathVariable Integer roomAppId,
	            @PathVariable Integer userId) {

	        RoomApprovalRequestDto response = roomApprovalRequestService.approveRoomRequest(roomAppId, userId);
	        return ResponseEntity.ok(response);
	    }
}
