package com.arinax.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.Post;
import com.arinax.entities.Room;
import com.arinax.entities.User;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.TurnmentApprovalDto;
import com.arinax.repositories.PostRepo;

import com.arinax.repositories.TurnmentApprovalRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.TurnmentApprovalService;

@Service
public class TurnmentApprovalServiceImpl implements TurnmentApprovalService{

	@Autowired
	PostRepo postRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	UserTransactionRepo userTransactionRepo;
	 @Autowired
	 private TurnmentApprovalRepo turnmentApprovalRepo;
	 
	@Override
	public TurnmentApprovalDto createTurnmentApproval(TurnmentApprovalDto turnmentApprovalDto, Integer postId,
			Integer userId) {
		 Post post = postRepo.findById(postId)
		            .orElseThrow(() -> new ResourceNotFoundException("Post", "Post ID", postId));

		    User user = userRepo.findById(userId)
		            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

		    if (user.getBalance() < post.getEntryFee()) {
		        throw new ApiException("Insufficient Balance");
		    }

		    if (post.getStatus() == Post.PostStatus.REJECTED ||
		        room.getStatus() == Room.RoomStatus.PRIVATE) {
		        throw new ApiException("This Room has already been Approved/Rejected.");
		    }
	}

	@Override
	public List<TurnmentApprovalDto> getAllApprovalsByPostId(Integer postId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TurnmentApprovalDto approvePostRequest(Integer postId, Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
