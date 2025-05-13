package com.arinax.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.TurnmentApproval;

public interface TurnmentApprovalRepo extends JpaRepository<TurnmentApproval,Integer>{

	List<TurnmentApproval> findByPost_PostIdAndStatus(Integer postId, TurnmentApproval.ApprovedStatus status);

	
}
