package com.arinax.services;

import java.util.List;

import com.arinax.playloads.TurnmentApprovalDto;

public interface TurnmentApprovalService {

	TurnmentApprovalDto createTurnmentApproval(TurnmentApprovalDto turnmentApprovalDto,Integer postId, Integer userId);

	List<TurnmentApprovalDto> getAllApprovalsByPostId(Integer postId);

	TurnmentApprovalDto approvePostRequest(Integer postAppId);
}
