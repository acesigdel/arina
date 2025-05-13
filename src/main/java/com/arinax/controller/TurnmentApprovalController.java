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

import com.arinax.playloads.TurnmentApprovalDto;
import com.arinax.services.TurnmentApprovalService;

@RestController
@RequestMapping("/api/v1/turnmentApp")
public class TurnmentApprovalController {

    @Autowired
    private TurnmentApprovalService turnmentApprovalService;

    // 1. Create a new Turnment Approval Request
    @PostMapping("/create/{postId}/user/{userId}")
    public ResponseEntity<TurnmentApprovalDto> createTurnmentApproval(
            @RequestBody TurnmentApprovalDto dto,
            @PathVariable Integer postId,
            @PathVariable Integer userId) {
        TurnmentApprovalDto created = turnmentApprovalService.createTurnmentApproval(dto, postId, userId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2. Get all pending approvals by postId
    @GetMapping("/pending/{postId}")
    public ResponseEntity<List<TurnmentApprovalDto>> getAllPendingApprovals(@PathVariable Integer postId) {
        List<TurnmentApprovalDto> list = turnmentApprovalService.getAllApprovalsByPostId(postId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 3. Approve a specific tournament approval request
    @PutMapping("/approve/{postAppId}")
    public ResponseEntity<TurnmentApprovalDto> approvePostRequest(@PathVariable Integer postAppId) {
        TurnmentApprovalDto approved = turnmentApprovalService.approvePostRequest(postAppId);
        return new ResponseEntity<>(approved, HttpStatus.OK);
    }
}
