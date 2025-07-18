package com.arinax.services;

import java.security.Principal;
import java.util.List;

import com.arinax.entities.Post.PostStatus;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;



public interface PostService {
	//create 

		PostDto createPost(PostDto postDto,Integer userId,Integer gameId, Integer modeId);

		//update 

		PostDto updatePost(PostDto postDto, Integer postId, Principal principal);

		// delete

		void deletePost(Integer postId);
		
		//get all posts
		
		PostResponse getAllPost(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
		
		//get single post
		
		PostDto getPostById(Integer postId);
		
		//get all posts by category
		
		
		
		//get all posts by user
		List<PostDto> getPostsByUser(Integer userId);
		
		//search posts
		List<PostDto> searchPosts(String keyword);

		List<PostDto> getPostsByGame(Integer gameId);

		//PostDto approvePost(Integer postId);

		PostDto rejectPost(Integer postId);

		//PostResponse getUnapprovedPosts(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

		PostResponse getPostsByStatus(PostStatus status, Integer pageNumber, Integer pageSize, String sortBy,
				String sortDir);

		

	}
