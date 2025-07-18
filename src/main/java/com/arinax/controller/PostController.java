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
import com.arinax.entities.Post;
import com.arinax.exceptions.ApiException;
import com.arinax.playloads.ApiResponse;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;
import com.arinax.playloads.RoomDto;
import com.arinax.services.FileService;
import com.arinax.services.PostService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/api/v1/")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
//	create
	@PostMapping("/user/{userId}/game/{gameId}/gamemode/{modeId}/posts")
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId,
			@PathVariable Integer gameId, @PathVariable Integer modeId) {
		PostDto createPost = this.postService.createPost(postDto, userId, gameId,modeId);
		return new ResponseEntity<PostDto>(createPost, HttpStatus.CREATED);
	}
	
	//update
	@PutMapping("/posts/{postId}")
	public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, 
			
			@PathVariable Integer postId,
			 Principal principal
			) {

		PostDto updatePost = this.postService.updatePost(postDto, postId,principal);
		return new ResponseEntity<PostDto>(updatePost, HttpStatus.OK);

	}

	// get by user

	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {

		List<PostDto> posts = this.postService.getPostsByUser(userId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);

	}

	// get by category

	@GetMapping("/game/{gameId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByGame(@PathVariable Integer gameId) {

		List<PostDto> posts = this.postService.getPostsByGame(gameId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);

	}

	// get all posts

	@GetMapping("/posts")
	public ResponseEntity<PostResponse> getAllPost(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
	}

	// ADMIN: Filter by Status (PENDING / REJECTED / APPROVED)
	@GetMapping("/posts/status/{status}")
	public ResponseEntity<PostResponse> getPostsByStatus(
	        @PathVariable("status") String statusStr,
	        @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
	        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
	        @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
	        @RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

	    Post.PostStatus status = Post.PostStatus.valueOf(statusStr.toUpperCase());
	    PostResponse response = postService.getPostsByStatus(status, pageNumber, pageSize, sortBy, sortDir);
	    return ResponseEntity.ok(response);
	}

	
	
	// get post details by id

	@GetMapping("/posts/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {

		PostDto postDto = this.postService.getPostById(postId);
		return new ResponseEntity<PostDto>(postDto, HttpStatus.OK);

	}

	
	
	// delete post
	@DeleteMapping("/posts/{postId}")
	public ApiResponse deletePost(@PathVariable Integer postId) {
		this.postService.deletePost(postId);
		return new ApiResponse("Post is successfully deleted !!", true);
	}

	// update post



	// search
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable("keywords") String keywords) {
		List<PostDto> result = this.postService.searchPosts(keywords);
		return new ResponseEntity<List<PostDto>>(result, HttpStatus.OK);
	}

	@PostMapping("/file/upload/{postId}/post")
	public ResponseEntity<PostDto> uploadPostFile(@RequestParam("file") MultipartFile file,
	                                                @RequestParam("fileType") String fileType,
	                                                @PathVariable Integer postId, Principal principal) throws IOException {
	   
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
	    PostDto postDto = this.postService.getPostById(postId);

	    // Smart setting of the file based on type
	    switch (fileType.toLowerCase()) {
	        case "screenshot1":
	            postDto.setImageName1(fileName);
	            break;
	        case "screenshot2":
	            postDto.setImageName2(fileName);
	            break;
	     
	        default:
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    PostDto updatedPost = this.postService.updatePost(postDto, postId, principal);
	    		
	    return new ResponseEntity<>(updatedPost, HttpStatus.OK);
	}

   
	// Method to serve files of various types
			@GetMapping(value = "post/image/{fileName}")
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
	
	
    @PutMapping("/post/{postId}/reject")
    public ResponseEntity<PostDto> rejectPost(@PathVariable Integer postId) {
        PostDto rejectedRequest = postService.rejectPost(postId);
        		
        return ResponseEntity.ok(rejectedRequest);
    }
//    @PutMapping("post/{postId}/approved")
//    public ResponseEntity<PostDto> approvedPost(@PathVariable Integer postId) {
//        PostDto approvedRequest = postService.approvePost(postId); 		
//        return ResponseEntity.ok(approvedRequest);
//    }
}
