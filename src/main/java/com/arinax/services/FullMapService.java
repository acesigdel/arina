package com.arinax.services;

import java.security.Principal;
import java.util.List;

import com.arinax.entities.FullMap.FullMapStatus;

import com.arinax.playloads.FullMapDto;
import com.arinax.playloads.FullMapResponse;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;

public interface FullMapService {

	//create 

			FullMapDto createFullMap(FullMapDto fullMapDto,Integer userId,Integer gameId, Integer modeId);

			//update 

			FullMapDto updateFullMap(FullMapDto fullMapDto, Integer  fullmapId, Principal principal);

			// delete

			void deleteFullMap(Integer  fullmapId);
			
			//get all posts
			
			FullMapResponse getAllFullMap(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
			
			//get single post
			
			FullMapDto getFullMapById(Integer  fullmapId);
			
			//get all posts by category
			
			
			
			//get all posts by user
			List<FullMapDto> getFullMapsByUser(Integer userId);
			
			//search posts
			List<FullMapDto> searchFullMaps(String keyword);

			List<FullMapDto> getFullMapsByGame(Integer gameId);

			//PostDto approvePost(Integer postId);

			FullMapDto rejectFullMap(Integer  fullmapId);

			//PostResponse getUnapprovedPosts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

			FullMapResponse getFullMapsByStatus(FullMapStatus status, Integer pageNumber, Integer pageSize, String sortBy,
					String sortDir);

			

}
