package com.arinax.services;

import java.util.List;

import com.arinax.entities.Room;
import com.arinax.playloads.GameDto;
import com.arinax.playloads.RoomDto;
import com.arinax.playloads.RoomResponse;



public interface RoomService {

	
	// create
	RoomDto createRoom(RoomDto roomDto, Integer userId, Integer gameId);

	// update
			RoomDto updateRoom(RoomDto roomDto, Integer roomId);

			// delete
			void deleteRoom(Integer roomId);

			// get

			RoomDto getRoomById(Integer roomId);

			// get All

			RoomResponse getAllRooms(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);


			List<RoomDto> getRoomsByGame(Integer gameId);


			List<RoomDto> getRoomsByUser(Integer userId);

			//List<RoomDto> searchRooms(Double keyword);

			List<RoomDto> searchRooms(Double entryFee, String gameType);

			
			//List<RoomDto> getRoomsByStatus(Room.RoomStatus status);

			


			


			

}
