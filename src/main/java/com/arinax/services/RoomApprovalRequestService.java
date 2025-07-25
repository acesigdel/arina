package com.arinax.services;

import java.util.List;

import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.TurnmentApproval.ApprovedStatus;
import com.arinax.playloads.RoomApprovalRequestDto;


public interface RoomApprovalRequestService {

	RoomApprovalRequestDto createRoomApproval(RoomApprovalRequestDto roomApprovalRequestDto,Integer roomId, Integer userId);

	//List<RoomApprovalRequestDto> getAllApprovalsByRoomId(Integer roomId);

	RoomApprovalRequestDto approveRoomRequest(Integer roomId, Integer userId);

	List<RoomApprovalRequestDto> getAllApprovalsByRoomId(Integer roomId,
			RoomApprovalRequest.ApprovedStatus status);

	//List<RoomApprovalRequestDto> getApprovalsByRoomIdAndStatus(Integer roomId, ApprovedStatus status);


	
	

}

