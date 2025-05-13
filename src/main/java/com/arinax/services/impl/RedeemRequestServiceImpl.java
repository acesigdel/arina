package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.RedeemRequest;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.RedeemRequestDto;
import com.arinax.repositories.RedeemRequestRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
import com.arinax.services.RedeemRequestService;
@Service
public class RedeemRequestServiceImpl implements RedeemRequestService{

	@Autowired
	UserRepo userRepo;
	@Autowired
	RedeemRequestRepo redeemRepo;
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	UserTransactionRepo userTransactionRepo;
	
	@Override
	public RedeemRequestDto RedeemBalance(RedeemRequestDto redeemRequestDto, Integer userId, Double balance) {
	    final double MIN_REDEEM = 121.0;

	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

	    if (balance < MIN_REDEEM) {
	        throw new ApiException("Minimum redeem amount is " + MIN_REDEEM);
	    }

	    if (user.getBalance() < balance) {
	        throw new ApiException("Insufficient balance. Available: " + user.getBalance());
	    }
	    RedeemRequest request = new RedeemRequest();
	    request.setUser(user);
	    request.setAmount(balance);
	    request.setAccountNo(redeemRequestDto.getAccountNo());
	    request.setPaymentGetway(redeemRequestDto.getPaymentGetway());
	    request.setStatus(RedeemRequest.Status.PENDING);
	    request.setRequestedAt(LocalDateTime.now());

	    redeemRepo.save(request);

	  return this.modelMapper.map(request, RedeemRequestDto.class);
	}
	
	@Override
	public void approveRedeemRequest(Long requestId) {
	    RedeemRequest request = redeemRepo.findById(requestId)
	            .orElseThrow(() -> new ResourceNotFoundException("RedeemRequest", "Id", requestId));

	    if (request.getStatus() != RedeemRequest.Status.PENDING) {
	        throw new ApiException("This request is already processed.");
	    }

	    User user = request.getUser();

	    if (user.getBalance() < request.getAmount()) {
	        throw new ApiException("User does not have enough balance.");
	    }

	    // Deduct balance
	    user.setBalance(user.getBalance() - request.getAmount());
	    userRepo.save(user);

	    UserTransaction txn = new UserTransaction();
        txn.setUser(user);
        txn.setAmount(-request.getAmount()); // decrease  means credited
        txn.setType("DEBITED");
        txn.setReason("You have Redeem The Coin");
        txn.setDateTime(LocalDateTime.now());
        userTransactionRepo.save(txn); // Save transaction
        
	    
	    request.setStatus(RedeemRequest.Status.APPROVED);
	    request.setProcessedAt(LocalDateTime.now());

	    this.redeemRepo.save(request);

	}

	// Get all pending requests (admin side)
	@Override
	public List<RedeemRequestDto> getAllPendingRequests() {
	    List<RedeemRequest> pendingRequests = redeemRepo.findByStatus(RedeemRequest.Status.PENDING);
	    return pendingRequests.stream()
	            .map(request -> modelMapper.map(request, RedeemRequestDto.class))
	            .collect(Collectors.toList());
	}

	// Get pending requests for a specific user
	@Override
	public List<RedeemRequestDto> getUserPendingRequests(Integer userId) {
	    List<RedeemRequest> pendingRequests = redeemRepo.findByUserIdAndStatus(userId, RedeemRequest.Status.PENDING);
	    return pendingRequests.stream()
	            .map(request -> modelMapper.map(request, RedeemRequestDto.class))
	            .collect(Collectors.toList());
	}

	// Get approved requests for a specific user
	@Override
	public List<RedeemRequestDto> getUserApprovedRequests(Integer userId) {
	    List<RedeemRequest> approvedRequests = redeemRepo.findByUserIdAndStatusOrderByProcessedAtDesc(userId, RedeemRequest.Status.APPROVED);
	    return approvedRequests.stream()
	            .map(request -> modelMapper.map(request, RedeemRequestDto.class))
	            .collect(Collectors.toList());
	}

}
