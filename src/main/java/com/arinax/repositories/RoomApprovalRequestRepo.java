package com.arinax.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.Room;
import com.arinax.entities.RoomApprovalRequest;

public interface RoomApprovalRequestRepo extends JpaRepository<RoomApprovalRequest, Integer>{

	List<RoomApprovalRequest> findByRoom(Room room);

}
