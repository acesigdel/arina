package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.arinax.entities.Game;
import com.arinax.entities.Post;
import com.arinax.entities.Room;
import com.arinax.entities.Room.RoomStatus;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.User;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.GameDto;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;
import com.arinax.playloads.RoomDto;
import com.arinax.playloads.RoomResponse;
import com.arinax.repositories.GameModeRepo;
import com.arinax.repositories.GameRepo;
import com.arinax.repositories.PostRepo;
import com.arinax.repositories.RoomRepo;
import com.arinax.repositories.UserRepo;

import com.arinax.services.RoomService;


@Service
public class RoomServiceImpl implements RoomService{

	 @Autowired
	    private RoomRepo roomRepo;

	    @Autowired
	    private ModelMapper modelMapper;

	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private GameRepo gameRepo;
	   

	@Override
	public RoomDto createRoom(RoomDto roomDto,Integer userId, Integer gameId) {

		User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

	    Game game = this.gameRepo.findById(gameId)
	            .orElseThrow(() -> new ResourceNotFoundException("Game", "game id ", gameId));
	    
	    Room room = this.modelMapper.map(roomDto, Room.class);
	    room.setAddedDate(LocalDateTime.now());
	    room.setGame(game);
	    room.setEntryFee(roomDto.getEntryFee());
	    
	   // String startTimeStr = roomDto.getStartTime(); // if it's String
	    //LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
	    //room.setStartTime(startTime);

	   room.setGameType(roomDto.getGameType());
	    room.setInventory(roomDto.getInventory());
	    room.setUser(user);
	    room.setContent(roomDto.getContent());
	    room.setStatus(Room.RoomStatus.PENDING);
	   
	    return this.modelMapper.map(room, RoomDto.class);
		
	}

	//room ko status pending nai rai ranxa , jaba samma creator le kunai user lai accept gardaina 
	//so player ko req aako xa 20m vitra samma creator le kunai user lai accept garyana vane disappear
	@Scheduled(fixedRate = 120000)
	public void markExpiredRoomsAsDisappear() {
	    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30); // filter recent rooms only
	    List<Room> rooms = roomRepo.findRecentPendingRooms(Room.RoomStatus.PENDING, cutoff);

	    LocalDateTime now = LocalDateTime.now();
	    for (Room room : rooms) {
	        if (room.getAddedDate().plusMinutes(20).isBefore(now)) {
	            room.setStatus(Room.RoomStatus.DISAPPEAR);

	            // Decrease balance from room creator
	            User user = room.getUser();
	            user.setBalance(user.getBalance() - 5);//
	            userRepo.save(user);
	            
	            roomRepo.save(room);
	        }
	    }
	}

	
	@Override
	public RoomDto updateRoom(RoomDto roomDto, Integer roomId) {
	    Room room = this.roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));

	    // update attributes from DTO
	    room.setContent(roomDto.getContent());
	    room.setEntryFee(roomDto.getEntryFee());
	    room.setWining(roomDto.getWining());
	    room.setGameType(roomDto.getGameType());
	    
	    room.setInventory(roomDto.getInventory());

	    // Handle start time conversion from String to LocalDateTime
//	    try {
//	        if (roomDto.getStartTime() != null) {
//	            LocalDateTime startTime = LocalDateTime.parse(roomDto.getStartTime());
//	            room.setStartTime(startTime);
//	        }
//	    } catch (DateTimeParseException e) {
//	        throw new RuntimeException("Invalid start time format: " + roomDto.getStartTime());
//	    }

	    Room updatedRoom = this.roomRepo.save(room);
	    return this.modelMapper.map(updatedRoom, RoomDto.class);
	}

	@Override
	public void deleteRoom(Integer roomId) {
	    Room room = this.roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
	    this.roomRepo.delete(room);
	}

	@Override
	public RoomDto getRoomById(Integer roomId) {
	    Room room = this.roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
	    return this.modelMapper.map(room, RoomDto.class);
	}


	
	
	@Override
	public RoomResponse getAllRooms(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
	    Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
	    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

	    // केवल PENDING status भएका rooms को page लिउँ
	    Page<Room> pageRoom = this.roomRepo.findByStatus(Room.RoomStatus.PENDING, pageable);

	    List<Room> allRooms = pageRoom.getContent();
	    List<RoomDto> roomDtos = allRooms.stream()
	            .map(room -> this.modelMapper.map(room, RoomDto.class))
	            .collect(Collectors.toList());

	    RoomResponse roomResponse = new RoomResponse();
	    roomResponse.setContent(roomDtos);
	    roomResponse.setPageNumber(pageRoom.getNumber());
	    roomResponse.setPageSize(pageRoom.getSize());
	    roomResponse.setTotalElements(pageRoom.getTotalElements());
	    roomResponse.setTotalPages(pageRoom.getTotalPages());
	    roomResponse.setLastPage(pageRoom.isLast());

	    return roomResponse;
	}


	@Override
    public List<RoomDto> getRoomsByGame(Integer gameId) {

        Game cat = this.gameRepo.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", gameId));
        List<Room> rooms = this.roomRepo.findByGame(cat);

        List<RoomDto> roomDtos = rooms.stream().map((room) -> this.modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());

        return roomDtos;
    }

	@Override
    public List<RoomDto> getRoomsByUser(Integer userId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
        List<Room> rooms = this.roomRepo.findByUser(user);

        List<RoomDto> roomDtos = rooms.stream().map((room) -> this.modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());

        return roomDtos;
    }

//	@Override
//	public List<RoomDto> searchRooms(Double keyword) {
//	    List<Room> rooms = this.roomRepo.searchByentryFee(keyword);
//	    return rooms.stream()
//	        .map(room -> this.modelMapper.map(room, RoomDto.class))
//	        .collect(Collectors.toList());
//	}

	@Override
	public List<RoomDto> searchRooms(Double entryFee, String gameType) {
	    List<Room> rooms = this.roomRepo.searchByEntryFeeAndGameType(entryFee, gameType);
	    return rooms.stream()
	        .map(room -> this.modelMapper.map(room, RoomDto.class))
	        .collect(Collectors.toList());
	}

	

}
