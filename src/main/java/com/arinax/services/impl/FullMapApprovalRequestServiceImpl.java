package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.FullMap;
import com.arinax.entities.FullMapApproval;
import com.arinax.entities.Post;
import com.arinax.entities.TurnmentApproval;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.FullMapApprovalDto;
import com.arinax.playloads.NotificationDto;
import com.arinax.playloads.TurnmentApprovalDto;
import com.arinax.repositories.FullMapApprovalRepo;
import com.arinax.repositories.FullMapRepo;
import com.arinax.repositories.PostRepo;
import com.arinax.repositories.TurnmentApprovalRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.FullMapApprovalService;
import com.arinax.services.NotificationService;

@Service
public class FullMapApprovalRequestServiceImpl implements FullMapApprovalService{


	@Autowired
	FullMapRepo fullMapRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	NotificationService notificationService;
	@Autowired
	UserTransactionRepo userTransactionRepo;
	
	 @Autowired
	 private FullMapApprovalRepo fullMapApprovalRepo;
	 
	@Override
	public FullMapApprovalDto createFullMapApproval(FullMapApprovalDto fullMapApprovalDto, Integer fullMapId,
			Integer userId) {
		 FullMap fullMap = fullMapRepo.findById(fullMapId)
		            .orElseThrow(() -> new ResourceNotFoundException("FullMap", "FullMap ID", fullMapId));

		    User user = userRepo.findById(userId)
		            .orElseThrow(() -> new ResourceNotFoundException("User", "User ID", userId));

		    if (user.getBalance() < fullMap.getEntryFee()) {
		        throw new ApiException("Insufficient Coin");
		    }

		    if (fullMap.getStatus() == FullMap.FullMapStatus.REJECTED ||//reject=game dismiss=rollback the coin
		    		fullMap.getStatus() == FullMap.FullMapStatus.PRIVATE) {//private=game started
		        throw new ApiException("This Room has already been Approved/Rejected.");
		    }
		    
		    FullMapApproval  tuap=new FullMapApproval();
		    tuap.setFullMap(fullMap);
		    tuap.setUser(user);
		    tuap.setStatus(FullMapApproval.ApprovedStatus.PENDING);
		    tuap.setRequestAt(LocalDateTime.now());
		    FullMapApproval saved = this.fullMapApprovalRepo.save(tuap);
		    return this.modelMapper.map(saved, FullMapApprovalDto.class);
	}
	//get all pending approval by postID
		@Override
		public List<FullMapApprovalDto> getAllApprovalsByFullMapId(Integer fullMapId) {
			
			 List<FullMapApproval> pendingApprovals =
					 fullMapApprovalRepo.findByFullMap_FullMapIdAndStatus(fullMapId, FullMapApproval.ApprovedStatus.PENDING);
			
		

			    return pendingApprovals.stream()
			        .map(approval -> modelMapper.map(approval, FullMapApprovalDto.class))
			        .collect(Collectors.toList());
			
}
		@Override
		public FullMapApprovalDto approveFullMapRequest(Integer fullMapAppId) {
		    FullMapApproval fullMapApproval = fullMapApprovalRepo.findById(fullMapAppId)
		            .orElseThrow(() -> new ResourceNotFoundException("FullMapApproval", "FullMapApproval ID", fullMapAppId));

		    User player = fullMapApproval.getUser();
		    double totalblc = player.getBalance();
		    double entryfee = fullMapApproval.getFullMap().getEntryFee();

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
		    fullMapApproval.setStatus(FullMapApproval.ApprovedStatus.CREATOR_APPROVED); // Assuming you have a field `approved` or similar
		    fullMapApprovalRepo.save( fullMapApproval);

		    // Convert to DTO and return
		    return modelMapper.map(fullMapApproval, FullMapApprovalDto.class);
		}

}
