package com.arinax.services;

import com.arinax.playloads.RoomApprovalRequestDto;


public interface RoomApprovalRequestService {

	RoomApprovalRequestDto createRoomApproval(RoomApprovalRequestDto roomApprovalRequestDto,Integer roomId, Integer userId);
	
}

