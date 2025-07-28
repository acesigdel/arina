package com.arinax.services;

import java.util.List;

import com.arinax.playloads.FullMapApprovalDto;

public interface FullMapApprovalService {

	FullMapApprovalDto createFullMapApproval(FullMapApprovalDto fullMapApprovalDto, Integer fullMapId, Integer userId);

	List<FullMapApprovalDto> getAllApprovalsByFullMapId(Integer fullMapId);

	FullMapApprovalDto approveFullMapRequest(Integer fullMapAppId);

}
