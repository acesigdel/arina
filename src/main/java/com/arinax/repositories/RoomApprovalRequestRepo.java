package com.arinax.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arinax.entities.Room;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.RoomApprovalRequest.ApprovedStatus;
import com.arinax.entities.User;

public interface RoomApprovalRequestRepo extends JpaRepository<RoomApprovalRequest, Integer>{

	List<RoomApprovalRequest> findByRoom(Room room);
	Optional<RoomApprovalRequest> findByRoomAndUser(Room room, User user);
	List<RoomApprovalRequest> findByRoomAndStatus(Room room, ApprovedStatus status);
//	List<RoomApprovalRequest> findByRoomAndStatus(Room room,
//			com.arinax.entities.RoomApprovalRequest.ApprovedStatus status);

}
