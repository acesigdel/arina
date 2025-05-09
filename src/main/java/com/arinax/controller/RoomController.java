package com.arinax.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;



import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arinax.exceptions.ApiException;
import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.GameDto;
import com.arinax.playloads.RoomDto;
import com.arinax.playloads.RoomResponse;
import com.arinax.services.FileService;
import com.arinax.services.RoomService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

	@Autowired
	private RoomService roomService;
	
	@Value("${project.image}")
	private String path;
	@Autowired
	private FileService fileService;
	
	
	@PostMapping("/file/upload/{roomId}")
	public ResponseEntity<RoomDto> uploadRoomFile(@RequestParam("file") MultipartFile file,
	                                                @RequestParam("fileType") String fileType,
	                                                @PathVariable Integer roomId) throws IOException {
	   
		 final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

		    // Check file size
		 if (file.getSize() > MAX_FILE_SIZE) {
			    throw new ApiException("File size must be less than 2MB");
			}

		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

	    if ( !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
	            && !fileExtension.equals("png")) {
	        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	    }

	    String fileName = this.fileService.uploadImage(path, file);
	    RoomDto roomDto = this.roomService.getRoomById(roomId);

	    // Smart setting of the file based on type
	    switch (fileType.toLowerCase()) {
	        case "creator_ss":
	            roomDto.setCreator_SS(fileName);
	            break;
	        case "player_ss":
	            roomDto.setPlayer_SS(fileName);
	            break;
	     
	        default:
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    RoomDto updatedRoom = this.roomService.updateRoom(roomDto, roomId);
	    return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
	}

   
	// Method to serve files of various types
			@GetMapping(value = "/image/{fileName}")
			public void downloadFile(
			        @PathVariable("fileName") String fileName,
			        HttpServletResponse response
			) throws IOException {
			    // Determine the file extension to set content type
			    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
			    MediaType mediaType;

			    switch (fileExtension) {
		        case "png":
		            mediaType = MediaType.IMAGE_PNG;
		            break;
		        case "jpg":
		        case "jpeg":
		            mediaType = MediaType.IMAGE_JPEG;
		            break;
		        default:
		            mediaType = MediaType.APPLICATION_OCTET_STREAM;
		    }

			    // Set the content type
			    response.setContentType(mediaType.toString());
			    
			    // Set the Content-Disposition header manually
			    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			    // Serve the file
			    try (InputStream resource = this.fileService.getResource(path, fileName)) {
			       
			    	if (resource == null) {
			            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			            return;
			    	//StreamUtils.copy(resource, response.getOutputStream());
			    }
			    	StreamUtils.copy(resource, response.getOutputStream());
			    } catch (FileNotFoundException e) {
			        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			    } catch (Exception e) {
			        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			    }
			}
	
	
	
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

