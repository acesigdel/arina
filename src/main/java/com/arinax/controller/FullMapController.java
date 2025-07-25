package com.arinax.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
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

import com.arinax.config.AppConstants;
import com.arinax.entities.FullMap;
import com.arinax.exceptions.ApiException;
import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.FullMapDto;
import com.arinax.playloads.FullMapResponse;

import com.arinax.services.FileService;
import com.arinax.services.FullMapService;


import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/v1/")
public class FullMapController {


	@Autowired
	private FullMapService fullMapService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
//	create
	@PostMapping("/user/{userId}/game/{gameId}/gamemode/{modeId}/fullmaps")
	public ResponseEntity<FullMapDto> createFullMap(@RequestBody FullMapDto fullMapDto, @PathVariable Integer userId,
			@PathVariable Integer gameId, @PathVariable Integer modeId) {
		FullMapDto createFullMap = this.fullMapService.createFullMap(fullMapDto, userId, gameId,modeId);
		return new ResponseEntity<FullMapDto>(createFullMap, HttpStatus.CREATED);
	}
	//update
		@PutMapping("/fullmaps/{fullmapId}")
		public ResponseEntity<FullMapDto> updateFullMap(@RequestBody FullMapDto fullMapDto, 
				
				@PathVariable Integer fullmapId,
				 Principal principal
				) {

			FullMapDto updatefullMap = this.fullMapService.updateFullMap(fullMapDto, fullmapId,principal);
			return new ResponseEntity<FullMapDto>(updatefullMap, HttpStatus.OK);

		}
	
		// get by user

		@GetMapping("/user/{userId}/fullmaps")
		public ResponseEntity<List<FullMapDto>> getFullMapsByUser(@PathVariable Integer userId) {
		    List<FullMapDto> fullMaps = this.fullMapService.getFullMapsByUser(userId);
		    return new ResponseEntity<>(fullMaps, HttpStatus.OK);
		}


		// get by category

		@GetMapping("/game/{gameId}/fullmaps")
		public ResponseEntity<List<FullMapDto>> getFullMapsByGame(@PathVariable Integer gameId) {
		    List<FullMapDto> fullMaps = this.fullMapService.getFullMapsByGame(gameId);
		    return new ResponseEntity<>(fullMaps, HttpStatus.OK);
		}

		
		@GetMapping("/fullmaps")
		public ResponseEntity<FullMapResponse> getAllFullMaps(
		        @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
		        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
		        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY_FullMAP, required = false) String sortBy,
		        @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {
			
		    FullMapResponse fullMapResponse = this.fullMapService.getAllFullMap(pageNumber, pageSize, sortBy, sortDir);
		    return new ResponseEntity<>(fullMapResponse, HttpStatus.OK);
		}
		@GetMapping("/fullmaps/status/{status}")
		public ResponseEntity<FullMapResponse> getFullMapsByStatus(
		        @PathVariable("status") String statusStr,
		        @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
		        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
		        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
		        @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		    FullMap.FullMapStatus status = FullMap.FullMapStatus.valueOf(statusStr.toUpperCase());
		    FullMapResponse fullMapResponse = this.fullMapService.getFullMapsByStatus(status, pageNumber, pageSize, sortBy, sortDir);
		    return new ResponseEntity<>(fullMapResponse, HttpStatus.OK);
		}

		@GetMapping("/fullmaps/{fullmapId}")
		public ResponseEntity<FullMapDto> getFullMapById(@PathVariable Integer fullmapId) {
		    FullMapDto fullMapDto = this.fullMapService.getFullMapById(fullmapId);
		    return new ResponseEntity<>(fullMapDto, HttpStatus.OK);
		}

		@DeleteMapping("/fullmaps/{fullMapId}")
		public ApiResponse deleteFullMap(@PathVariable Integer fullMapId) {
		    this.fullMapService.deleteFullMap(fullMapId);
		    return new ApiResponse("FullMap is successfully deleted !!", true);
		}

		@GetMapping("/fullmaps/search/{keywords}")
		public ResponseEntity<List<FullMapDto>> searchFullMapByTitle(@PathVariable("keywords") String keywords) {
		    List<FullMapDto> result = this.fullMapService.searchFullMaps(keywords);
		    return new ResponseEntity<>(result, HttpStatus.OK);
		}

		@PutMapping("/fullmap/{fullMapId}/reject")
		public ResponseEntity<FullMapDto> rejectFullMap(@PathVariable Integer fullMapId) {
		    FullMapDto rejectedMap = fullMapService.rejectFullMap(fullMapId);
		    return ResponseEntity.ok(rejectedMap);
		}

		@PostMapping("/file/upload/{fullMapId}/fullmap")
		public ResponseEntity<FullMapDto> uploadFullMapFile(@RequestParam("file") MultipartFile file,
		                                                    @RequestParam("fileType") String fileType,
		                                                    @PathVariable Integer fullMapId,
		                                                    Principal principal) throws IOException {

		    final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

		    if (file.getSize() > MAX_FILE_SIZE) {
		        throw new ApiException("File size must be less than 2MB");
		    }

		    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

		    if (!fileExtension.equals("jpeg") && !fileExtension.equals("jpg") && !fileExtension.equals("png")) {
		        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		    }

		    String fileName = this.fileService.uploadImage(path, file);
		    FullMapDto fullMapDto = this.fullMapService.getFullMapById(fullMapId); // You should have this method

		    switch (fileType.toLowerCase()) {
		        case "screenshot1":
		            fullMapDto.setImageName1(fileName);
		            break;
		        case "screenshot2":
		            fullMapDto.setImageName2(fileName);
		            break;
		        default:
		            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		    }

		    FullMapDto updatedFullMap = this.fullMapService.updateFullMap(fullMapDto, fullMapId, principal);
		    return new ResponseEntity<>(updatedFullMap, HttpStatus.OK);
		}

		@GetMapping(value = "/fullmap/image/{fileName}")
		public void downloadFullMapFile(
		        @PathVariable("fileName") String fileName,
		        HttpServletResponse response) throws IOException {

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

		    response.setContentType(mediaType.toString());
		    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		    try (InputStream resource = this.fileService.getResource(path, fileName)) {
		        if (resource == null) {
		            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		            return;
		        }
		        StreamUtils.copy(resource, response.getOutputStream());
		    } catch (FileNotFoundException e) {
		        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    } catch (Exception e) {
		        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    }
		}

}

