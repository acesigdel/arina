package com.arinax.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.GameDto;
import com.arinax.playloads.RoomDto;
import com.arinax.playloads.RoomResponse;
import com.arinax.services.RoomService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

	@Autowired
	private RoomService roomService;

//	create
	@PostMapping("/user/{userId}/game/{gameId}")
	public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto, @PathVariable Integer userId,
			@PathVariable Integer gameId) {
		RoomDto createRoom = this.roomService.createRoom(roomDto, userId, gameId);
		return new ResponseEntity<RoomDto>(createRoom, HttpStatus.CREATED);
	}

	// Update
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(
            @RequestBody RoomDto roomDto,
            @PathVariable Integer roomId) {

        RoomDto updatedRoom = this.roomService.updateRoom(roomDto, roomId);
        return ResponseEntity.ok(updatedRoom);
    }

    // Delete
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable Integer roomId) {
        this.roomService.deleteRoom(roomId);
        return new ResponseEntity<>(new ApiResponse("Room deleted successfully", true), HttpStatus.OK);
    }

    // Get one room
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable Integer roomId) {
        RoomDto roomDto = this.roomService.getRoomById(roomId);
        return ResponseEntity.ok(roomDto);
    }

    // Get all rooms (pagination + sorting)
    @GetMapping("/")
    public ResponseEntity<RoomResponse> getAllRooms(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "roomId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {

        RoomResponse response = this.roomService.getAllRooms(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
 // Get rooms by game
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<RoomDto>> getRoomsByGame(@PathVariable Integer gameId) {
        List<RoomDto> rooms = this.roomService.getRoomsByGame(gameId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // Get rooms by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoomDto>> getRoomsByUser(@PathVariable Integer userId) {
        List<RoomDto> rooms = this.roomService.getRoomsByUser(userId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
    
// // Search Room by entry fee
//    @GetMapping("/rooms/search/{entryFee}")
//    public ResponseEntity<List<RoomDto>> searchRoomByEntryFee(@PathVariable Double entryFee) {
//        List<RoomDto> rooms = this.roomService.searchRooms(entryFee);
//        return new ResponseEntity<>(rooms, HttpStatus.OK);
//    }
//GET /api/v1/rooms/search?entryFee=100.0&gameType=2v2
    @GetMapping("/search")
    public ResponseEntity<List<RoomDto>> searchRoom(
            @RequestParam(required = false) Double entryFee,
            @RequestParam(required = false) String gameType) {

        List<RoomDto> rooms = roomService.searchRooms(entryFee, gameType);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

}

