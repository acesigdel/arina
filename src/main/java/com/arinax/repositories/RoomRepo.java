package com.arinax.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arinax.entities.Game;

import com.arinax.entities.Room;
import com.arinax.entities.Room.RoomStatus;
import com.arinax.entities.User;

public interface RoomRepo extends JpaRepository<Room, Integer> {

	List<Room> findByUser(User user);
	List<Room> findByGame(Game game);	
	
	@Query("SELECT r FROM Room r WHERE r.entryFee = :key")
	List<Room> searchByentryFee(@Param("key") Double key);

	@Query("SELECT r FROM Room r WHERE r.entryFee = :entryFee AND r.gameType = :gameType")
	List<Room> searchByEntryFeeAndGameType(@Param("entryFee") Double entryFee, @Param("gameType") String gameType);
	
	//List<Room> findByStatus(RoomStatus pending);
	Page<Room> findByStatus(Room.RoomStatus status, Pageable pageable);

	
	@Query("SELECT r FROM Room r WHERE r.status = :status AND r.addedDate >= :cutoffTime")
	List<Room> findRecentPendingRooms(@Param("status") Room.RoomStatus status, @Param("cutoffTime") LocalDateTime cutoffTime);
	
	List<Room> findByStatus(Room.RoomStatus status);


}
