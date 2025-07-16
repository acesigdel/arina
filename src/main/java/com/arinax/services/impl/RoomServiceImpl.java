package com.arinax.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.arinax.entities.Game;
import com.arinax.entities.Post;
import com.arinax.entities.Room;
import com.arinax.entities.Room.RoomStatus;
import com.arinax.entities.RoomApprovalRequest;
import com.arinax.entities.User;
import com.arinax.entities.UserTransaction;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.GameDto;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;
import com.arinax.playloads.RoomDto;
import com.arinax.playloads.RoomResponse;
import com.arinax.repositories.GameModeRepo;
import com.arinax.repositories.GameRepo;
import com.arinax.repositories.PostRepo;
import com.arinax.repositories.RoomApprovalRequestRepo;
import com.arinax.repositories.RoomRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.repositories.UserTransactionRepo;
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
	   
	    @Autowired
	    UserTransactionRepo  userTransactionRepo;
	    @Autowired
	    RoomApprovalRequestRepo roomApprovalRequestRepo;
	@Override
	public RoomDto createRoom(RoomDto roomDto,Integer userId, Integer gameId) {

		User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

	    Game game = this.gameRepo.findById(gameId)
	            .orElseThrow(() -> new ResourceNotFoundException("Game", "game id ", gameId));
	    
	    Room room = this.modelMapper.map(roomDto, Room.class);
	    room.setAddedDate(LocalDateTime.now());
	    room.setGame(game);
	    
	    double blc=user.getBalance();
	    if(blc<roomDto.getEntryFee()) {
	    	throw new ApiException("Insufficient Balance");
	    }
	    room.setEntryFee(roomDto.getEntryFee());
	  //reducing balance  
	  user.setBalance(user.getBalance()-roomDto.getEntryFee());

	   room.setGameType(roomDto.getGameType());
	    room.setInventory(roomDto.getInventory());
	    room.setCreator_SS("");
	    room.setPlayer_SS("");
	    room.setUser(user);
	    room.setContent(roomDto.getContent());
	    room.setStatus(Room.RoomStatus.PENDING);
	    //----------------------------------------------------------------
	    UserTransaction txn = new UserTransaction();
        txn.setUser(user);
        txn.setAmount(-roomDto.getEntryFee()); // negative means debit
        txn.setType("DEBITED");
        txn.setReason("coin is added in Your account");
        txn.setDateTime(LocalDateTime.now());
        //-----------------------------------------------------
        userTransactionRepo.save(txn); // Save transaction
	    userRepo.save(user);
	    Room savedRoom = roomRepo.save(room);
	    return this.modelMapper.map(savedRoom, RoomDto.class);
		
	}

	//room ko status pending nai rai ranxa , jaba samma creator le kunai user lai accept gardaina 
	//so player ko req aako xa 20m vitra samma creator le kunai user lai accept garyana vane disappear
	@Transactional
	@Scheduled(fixedRate = 120000)
	public void markExpiredRoomsAsDisappear() {
	    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30); // filter recent rooms only
	    List<Room> rooms = roomRepo.findRecentPendingRooms(Room.RoomStatus.PENDING, cutoff);
	    LocalDateTime now = LocalDateTime.now();
	   
	    for (Room room : rooms) {
	        if (room.getAddedDate().plusMinutes(20).isBefore(now)) {
	        	double deduction=0.0;
	        	double entryFee = room.getEntryFee();
	        	User user = room.getUser();
	            user.setBalance(user.getBalance() + entryFee); 
	       
	         // CREDIT transaction
	            UserTransaction creditTxn = new UserTransaction();
	            creditTxn.setUser(user);
	            creditTxn.setAmount(entryFee);
	            creditTxn.setType("CREDITED");
	            creditTxn.setReason("Room entry fee returned due to no approval");
	            creditTxn.setDateTime(LocalDateTime.now());
	            userTransactionRepo.save(creditTxn);
	            
	        	List<RoomApprovalRequest> requests = roomApprovalRequestRepo.findByRoom(room);
	        	 if (!requests.isEmpty()) {
	        		    roomApprovalRequestRepo.deleteAll(requests);
	        		
	        		     deduction = entryFee * 0.13;
	        		     user.setBalance(user.getBalance() - deduction); 
	        	
	        		  // DEBIT transaction
	        		     UserTransaction debitTxn = new UserTransaction();
	        		     debitTxn.setUser(user);
	        		     debitTxn.setAmount(-deduction);
	        		     debitTxn.setType("DEBITED");
	        		     debitTxn.setReason("13% penalty for no approval");
	        		     debitTxn.setDateTime(LocalDateTime.now());
	        		     userTransactionRepo.save(debitTxn);
	        	 }

	        	room.setStatus(Room.RoomStatus.DISAPPEAR);
	            userRepo.save(user);
	            
	            roomRepo.save(room);
	        }
	    }
	}

	//[room request approved ] garna baki
			
			
	@Override
	public RoomDto updateRoom(RoomDto roomDto, Integer roomId) {
	    Room room = this.roomRepo.findById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));

	    Optional.ofNullable(roomDto.getContent()).ifPresent(room::setContent);
	    Optional.ofNullable(roomDto.getEntryFee()).ifPresent(room::setEntryFee);
	    Optional.ofNullable(roomDto.getWining()).ifPresent(room::setWining);
	    Optional.ofNullable(roomDto.getGameType()).ifPresent(room::setGameType);
	    Optional.ofNullable(roomDto.getCreator_SS()).ifPresent(room::setCreator_SS);
	    Optional.ofNullable(roomDto.getPlayer_SS()).ifPresent(room::setPlayer_SS);
	    Optional.ofNullable(roomDto.getInventory()).ifPresent(room::setInventory);

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
	public List<RoomDto> getRoomsByStatus(Room.RoomStatus status) {
	    List<Room> rooms = roomRepo.findByStatus(status);
	    return rooms.stream()
	            .map(room -> modelMapper.map(room, RoomDto.class))
	            .collect(Collectors.toList());
	}

//	@Override
//	public List<RoomDto> getRoomsByGameId(Integer gameId) {
//	    Game game = gameRepo.findById(gameId)
//	                .orElseThrow(() -> new ResourceNotFoundException("Game", "gameId", gameId));
//	    List<Room> rooms = roomRepo.findByGame(game);
//	    List<RoomDto> roomDtos = rooms.stream()
//	                                 .map(room -> modelMapper.map(room, RoomDto.class))
//	                                 .collect(Collectors.toList());
//	    return roomDtos;
//	}


	
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
