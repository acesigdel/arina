package com.arinax.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.RoomApprovalRequest;

public interface RoomApprovalRequestRepo extends JpaRepository<RoomApprovalRequest, Integer>{

}
