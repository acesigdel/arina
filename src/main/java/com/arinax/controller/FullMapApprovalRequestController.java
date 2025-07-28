package com.arinax.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.playloads.FullMapApprovalDto;
import com.arinax.playloads.TurnmentApprovalDto;
import com.arinax.services.FullMapApprovalService;
import com.arinax.services.TurnmentApprovalService;

@RestController
@RequestMapping("/api/v1/fullmapApp")
public class FullMapApprovalRequestController {

	  @Autowired
	    private FullMapApprovalService fullMapApprovalService;

	    // 1. Create a new Turnment Approval Request
	    @PostMapping("/create/{fullMapId}/user/{userId}")
	    public ResponseEntity<FullMapApprovalDto> createTurnmentApproval(
	            @RequestBody FullMapApprovalDto dto,
	            @PathVariable Integer fullMapId,
	            @PathVariable Integer userId) {
	        FullMapApprovalDto created = fullMapApprovalService.createFullMapApproval(dto, fullMapId, userId);
	        	
	        return new ResponseEntity<>(created, HttpStatus.CREATED);
	    }
	    
	    // 2. Get all pending approvals by postId
	    @GetMapping("/pending/{fullMapId}")
	    public ResponseEntity<List<FullMapApprovalDto>> getAllPendingApprovals(@PathVariable Integer fullMapId) {
	        List<FullMapApprovalDto> list = fullMapApprovalService.getAllApprovalsByFullMapId(fullMapId);
	        return new ResponseEntity<>(list, HttpStatus.OK);
	    }

	    // 3. Approve a specific tournament approval request
	    @PutMapping("/approve/{fullMapAppId}")
	    public ResponseEntity<FullMapApprovalDto> approvePostRequest(@PathVariable Integer fullMapAppId) {
	        FullMapApprovalDto approved = fullMapApprovalService.approveFullMapRequest(fullMapAppId);
	        return new ResponseEntity<>(approved, HttpStatus.OK);
	    }
}
