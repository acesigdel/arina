package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.Post;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.TurnmentApproval;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.NotificationDto;
import com.arinax.playloads.TurnmentApprovalDto;
import com.arinax.repositories.PostRepo;

import com.arinax.repositories.TurnmentApprovalRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.NotificationService;
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
	NotificationService notificationService;
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
		        throw new ApiException("Insufficient Coin");
		    }

		    if (post.getStatus() == Post.PostStatus.REJECTED ||//reject=game dismiss=rollback the coin
		        post.getStatus() == Post.PostStatus.PRIVATE) {//private=game started
		        throw new ApiException("This Room has already been Approved/Rejected.");
		    }
		    
		    TurnmentApproval  tuap=new TurnmentApproval();
		    tuap.setPost(post);
		    tuap.setUser(user);
		    tuap.setStatus(TurnmentApproval.ApprovedStatus.PENDING);
		    tuap.setRequestAt(LocalDateTime.now());
		    TurnmentApproval saved = this.turnmentApprovalRepo.save(tuap);
		    return this.modelMapper.map(saved, TurnmentApprovalDto.class);
	}

	//get all pending approval by postID
	@Override
	public List<TurnmentApprovalDto> getAllApprovalsByPostId(Integer postId) {
		
		 List<TurnmentApproval> pendingApprovals =
				 turnmentApprovalRepo.findByPost_PostIdAndStatus(postId, TurnmentApproval.ApprovedStatus.PENDING);
		
		    return pendingApprovals.stream()
		        .map(approval -> modelMapper.map(approval, TurnmentApprovalDto.class))
		        .collect(Collectors.toList());
		}

	//
	@Override
	public TurnmentApprovalDto approvePostRequest(Integer postAppId) {
	    TurnmentApproval turnmentApproval = turnmentApprovalRepo.findById(postAppId)
	            .orElseThrow(() -> new ResourceNotFoundException("TurnmentApproval", "TurnmentApproval ID", postAppId));

	    User player = turnmentApproval.getUser();
	    double totalblc = player.getBalance();
	    double entryfee = turnmentApproval.getPost().getEntryFee();

	    if (totalblc < entryfee) {
	        throw new ApiException("Insufficient Coin of player");
	    }

	    // Deduct balance
	    player.setBalance(totalblc - entryfee);

	    // Send notification
	    NotificationDto notificationDto = new NotificationDto();
    	 notificationDto.setMessage( "You are selected for the game");
        notificationService.createNotification(notificationDto, player.getId(),null);

	    // Save transaction
	    UserTransaction txn = new UserTransaction();
	    txn.setUser(player);
	    txn.setAmount(-entryfee); // negative = debit
	    txn.setType("DEBITED");
	    txn.setReason("Game Entry Fee");
	    txn.setDateTime(LocalDateTime.now());
	    userTransactionRepo.save(txn);

	    // Update and save approval status
	    turnmentApproval.setStatus(TurnmentApproval.ApprovedStatus.CREATOR_APPROVED); // Assuming you have a field `approved` or similar
	    turnmentApprovalRepo.save(turnmentApproval);

	    // Convert to DTO and return
	    return modelMapper.map(turnmentApproval, TurnmentApprovalDto.class);
	}




}
