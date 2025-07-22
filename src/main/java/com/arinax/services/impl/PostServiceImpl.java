package com.arinax.services.impl;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arinax.config.AppConstants;
import com.arinax.entities.Game;
import com.arinax.entities.GameMode;
import com.arinax.entities.Post;
import com.arinax.entities.Role;
import com.arinax.entities.User;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.NotificationDto;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;
import com.arinax.repositories.GameModeRepo;
import com.arinax.repositories.GameRepo;
import com.arinax.repositories.PostRepo;
import com.arinax.repositories.RoleRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.services.NotificationService;
import com.arinax.services.PostService;




@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private GameRepo gameRepo;
    
    @Autowired
    private GameModeRepo modeRepo;
    
    @Autowired
    private RoleRepo roleRepo;
    
    @Autowired
    private NotificationService notificationService;

    
    @Override
    public PostDto createPost(PostDto postDto, Integer userId, Integer gameId,Integer modeId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

        Game game = this.gameRepo.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id ", gameId));
        
        GameMode mode = this.modeRepo.findById(modeId)
                .orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id ", modeId));
        
        Role adminRole = roleRepo.findById(AppConstants.ADMIN_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.ADMIN_USER));

        boolean isAdmin = user.getRoles()
        	    .stream()
        	    .anyMatch(role -> role.getName().equals(adminRole.getName()));

        	if (!isAdmin) {
        	    throw new ApiException("Only admin can create Tournament");
        	}


        Post post = this.modelMapper.map(postDto, Post.class);
        
        post.setImageName1("");
        post.setImageName2("");
        
        post.setAddedDate(LocalDateTime.now());
        post.setUser(user);
        post.setGame(game);
        post.setGameMode(mode);
        post.setContent(postDto.getContent());
        post.setEntryFee(postDto.getEntryFee());
        
        LocalDateTime startTime = postDto.getStartTime();

        if (startTime == null) {
            throw new ApiException("Start time cannot be null");
        }

        if (startTime.isBefore(LocalDateTime.now())) {
            throw new ApiException("Start time must be in the future");
        }

        post.setStartTime(startTime);  
   
            post.setStatus(Post.PostStatus.PENDING);
          
        //notification to all user 
      Post newPost = this.postRepo.save(post);  
        
        return this.modelMapper.map(newPost, PostDto.class);
    }
    @Override
    public PostDto updatePost(PostDto postDto, Integer postId,Principal principal) {

        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "post id", postId));

        Role adminRole = roleRepo.findById(AppConstants.ADMIN_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.ADMIN_USER));
        
        //List<User> allAdmins = userRepo.findAllByRoleName(adminRole.getName());
        
        if (post.getStatus() == Post.PostStatus.PRIVATE || post.getStatus() == Post.PostStatus.PLAYER_APPROVED) {
            throw new ApiException("You can't update during private and player approved status");
        }


        String username=  principal.getName();
        
        User user = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", username));

        
        boolean isAdmin = user.getRoles()
        	    .stream()
        	    .anyMatch(role -> role.getName().equals(adminRole.getName()));

        	if (!isAdmin) {
        	    throw new ApiException("Only admin can Update  Tournament");
        	}


        Game game = this.gameRepo.findById(postDto.getGame().getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", postDto.getGame().getGameId()));

        GameMode mode = this.modeRepo.findById(postDto.getGameMode().getModeId())
                .orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id", postDto.getGameMode().getModeId()));
        
        if(postDto.getTitle()!=null) {
        post.setTitle(postDto.getTitle());
        }
        if(postDto.getContent()!=null) {
        post.setContent(postDto.getContent());
        }
        if(postDto.getEntryFee()!=null) {
        	post.setEntryFee(postDto.getEntryFee());
        }
        if(postDto.getImageName1()!=null) {
        post.setImageName1(postDto.getImageName1());
        }
        if(postDto.getImageName2()!=null) {
            post.setImageName2(postDto.getImageName2());
            }
        post.setGame(game);
        post.setGameMode(mode);
        post.setStatus(Post.PostStatus.PENDING);
            // Admin updated the post => confirm notification
//            NotificationDto notificationDto = new NotificationDto();
//       	 notificationDto.setMessage("Your post has been updated and approved successfully.");
//           notificationService.createNotification(notificationDto, user.getId(),null);
           
       
        Post updatedPost = this.postRepo.save(post);
        return this.modelMapper.map(updatedPost, PostDto.class);
    }


    
//    @Override
    
//    public PostDto approvePost(Integer postId) {
//    	 Post post = this.postRepo.findById(postId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", postId));
//
//        // Only allow approval if status is PENDING
//        if (post.getStatus() != Post.PostStatus.PENDING) {//pending hunai paro natra approved hudaina
//            throw new ApiException("Cannot approve.");
//        }
//
//        post.setStatus(Post.PostStatus.APPROVED);
//        NotificationDto notificationDto = new NotificationDto();
//     	 notificationDto.setMessage( "Your post titled '" + post.getTitle() + "' has been APPROVED.");
//         notificationService.createNotification(notificationDto, post.getUser().getId(),null);
//
//        Post approvedPost = this.postRepo.save(post);
//        return this.modelMapper.map(approvedPost, PostDto.class);
//    }
//    
    @Override
    public PostDto rejectPost(Integer postId) {
    	 Post post = this.postRepo.findById(postId)
                 .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", postId));

        // Only allow approval if status is PENDING
        if (post.getStatus() != Post.PostStatus.PENDING) { //pending hunai paro
            throw new ApiException("Turnment Can only Reject In Panding Status");
        }

        post.setStatus(Post.PostStatus.REJECTED);
       
//        NotificationDto notificationDto = new NotificationDto();
//    	 notificationDto.setMessage( "Your post titled '" + post.getTitle() + "' has been REJECTED.");
//        notificationService.createNotification(notificationDto, post.getUser().getId(),null);

        Post approvedPost = this.postRepo.save(post);
        return this.modelMapper.map(approvedPost, PostDto.class);
    }
    


    
    @Override
    public void deletePost(Integer postId) {

        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", postId));

        this.postRepo.delete(post);

    }

    @Override
    public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
        
        Page<Post> pagePost = this.postRepo.findByStatus(Post.PostStatus.PENDING, p);

       // Page<Post> pagePost = this.postRepo.findAll(p);

        List<Post> allPosts = pagePost.getContent();

        List<PostDto> postDtos = allPosts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();

        postResponse.setContent(postDtos);
        postResponse.setPageNumber(pagePost.getNumber());
        postResponse.setPageSize(pagePost.getSize());
        postResponse.setTotalElements(pagePost.getTotalElements());

        postResponse.setTotalPages(pagePost.getTotalPages());
        postResponse.setLastPage(pagePost.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(Integer postId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "post id", postId));
        return this.modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> getPostsByGame(Integer gameId) {

        Game cat = this.gameRepo.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", gameId));
        List<Post> posts = this.postRepo.findByGame(cat);

        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return postDtos;
    }

    @Override
    public List<PostDto> getPostsByUser(Integer userId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
        List<Post> posts = this.postRepo.findByUser(user);

        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return postDtos;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = this.postRepo.searchByTitle("%" + keyword + "%");
        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
        return postDtos;
    }
    //getUnapprovedPosts(0, 10, "addedDate", "desc");

    @Override
    public PostResponse getPostsByStatus(Post.PostStatus status, Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
       
    	Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Post> pagePost = postRepo.findByStatus(status, pageable);

        List<PostDto> postDtos = pagePost.getContent()
            .stream()
            .map(post -> modelMapper.map(post, PostDto.class))
            .collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setContent(postDtos);
        response.setPageNumber(pagePost.getNumber());
        response.setPageSize(pagePost.getSize());
        response.setTotalElements(pagePost.getTotalElements());
        response.setTotalPages(pagePost.getTotalPages());
        response.setLastPage(pagePost.isLast());

        return response;
    }


   
}

//@Override
//public PostDto createPost(PostDto postDto, Integer userId, Integer gameId,Integer modeId) {
//
//    User user = this.userRepo.findById(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));
//
//    Game game = this.gameRepo.findById(gameId)
//            .orElseThrow(() -> new ResourceNotFoundException("Game", "game id ", gameId));
//    
//    GameMode mode = this.modeRepo.findById(modeId)
//            .orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id ", modeId));
//    
//    Role adminRole = roleRepo.findById(AppConstants.ADMIN_USER)
//            .orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.ADMIN_USER));
//
//    List<User> allAdmins = userRepo.findAllByRoleName(adminRole.getName());
//
//    Post post = this.modelMapper.map(postDto, Post.class);
//    post.setImageName("default.png");
//    
//    post.setAddedDate(LocalDateTime.now());
//    post.setUser(user);
//    post.setGame(game);
//    post.setGameMode(mode);
//    post.setContent(postDto.getContent());
//    post.setEntryFee(postDto.getEntryFee());
//    
//    LocalDateTime startTime = postDto.getStartTime();
//
//    if (startTime == null) {
//        throw new ApiException("Start time cannot be null");
//    }
//
//    if (startTime.isBefore(LocalDateTime.now())) {
//        throw new ApiException("Start time must be in the future");
//    }
//
//    post.setStartTime(startTime);  
//    if (user.getRoles().stream().anyMatch(role -> role.getName().equals(adminRole.getName()))) {
//        post.setStatus(Post.PostStatus.APPROVED);
//       
//    } else {
//        post.setStatus(Post.PostStatus.PENDING);
//      
//    }
//  Post newPost = this.postRepo.save(post);
//
//    if (user.getRoles().stream().anyMatch(role -> role.getName().equals(adminRole.getName()))) {
//        // Admin created post => approved directly
//    	 NotificationDto notificationDto = new NotificationDto();
//         notificationDto.setMessage("Your Turnment has been created successfully.");
//            
//            notificationService.createNotification(notificationDto, user.getId(),null);
//    	
//    } else {
//        // Normal user created post => pending
//    	 NotificationDto notificationDto = new NotificationDto();
//    	 notificationDto.setMessage("Your Turnment has been submitted and is under review.");
//        notificationService.createNotification(notificationDto, user.getId(),null);
//        //----------------------------------------------------------------------------
//        
//        for (User admin : allAdmins) {
//            notificationService.sendNotificationToUsers(
//                Collections.singletonList(admin.getId()),
//                "A new Turnment needs approval.",
//                null
//            );
//        }
//
//    }
//    return this.modelMapper.map(newPost, PostDto.class);
//}

//@Override
//public PostDto updatePost(PostDto postDto, Integer postId,Principal principal) {
//
//    Post post = this.postRepo.findById(postId)
//            .orElseThrow(() -> new ResourceNotFoundException("Post", "post id", postId));
//
//    Role adminRole = roleRepo.findById(AppConstants.ADMIN_USER)
//            .orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.ADMIN_USER));
//    
//    List<User> allAdmins = userRepo.findAllByRoleName(adminRole.getName());
//
//    String username=  principal.getName();
//    User user = this.userRepo.findByEmail(username)
//            .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", username));
//
//    
//
//    boolean isAdmin = user.getRoles().stream()
//            .anyMatch(role -> role.getName().equals(adminRole.getName()));
//
//    if (!isAdmin) {
//        // Normal user only allowed if post is PENDING or REJECTED//pending ra reject ma xina vane rokxa
//        if (!(post.getStatus() == Post.PostStatus.PENDING || post.getStatus() == Post.PostStatus.REJECTED)) {
//            throw new ApiException("You can only edit posts that are Pending or Rejected.");
//        }
//    }
//
//    Game game = this.gameRepo.findById(postDto.getGame().getGameId())
//            .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", postDto.getGame().getGameId()));
//
//    GameMode mode = this.modeRepo.findById(postDto.getGameMode().getModeId())
//            .orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id", postDto.getGameMode().getModeId()));
//    
//    post.setTitle(postDto.getTitle());
//    post.setContent(postDto.getContent());
//   // post.setImageName(postDto.getImageName());
//    post.setGame(game);
//    post.setGameMode(mode);
//
//    if (isAdmin) {
//        post.setStatus(Post.PostStatus.APPROVED);
//        // Admin updated the post => confirm notification
//        NotificationDto notificationDto = new NotificationDto();
//   	 notificationDto.setMessage("Your post has been updated and approved successfully.");
//       notificationService.createNotification(notificationDto, user.getId(),null);
//       
//    } else {
//        post.setStatus(Post.PostStatus.PENDING);
//        NotificationDto notificationDto = new NotificationDto();
//      	 notificationDto.setMessage("Your post has been updated and is under review.");
//          notificationService.createNotification(notificationDto, user.getId(),null);
//       
//          
//         
//        // Notify all admins
//        
//        	 for (User admin : allAdmins) {
//                 notificationService.sendNotificationToUsers(
//                     Collections.singletonList(admin.getId()),
//                     "A post has been updated by a user and needs review.",
//                     null
//                 );
//             
//        }
//    }
//    Post updatedPost = this.postRepo.save(post);
//    return this.modelMapper.map(updatedPost, PostDto.class);
//}
