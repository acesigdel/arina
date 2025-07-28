package com.arinax.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.FullMapApproval;



public interface FullMapApprovalRepo extends JpaRepository<FullMapApproval,Integer>{
	
	List<FullMapApproval> findByFullMap_FullMapIdAndStatus(Integer fullMapId, FullMapApproval.ApprovedStatus status);
}
