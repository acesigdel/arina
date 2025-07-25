package com.arinax.services.impl;

import java.security.Principal;
import java.time.LocalDateTime;
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
import com.arinax.entities.FullMap;
import com.arinax.entities.FullMap.FullMapStatus;
import com.arinax.entities.Game;
import com.arinax.entities.GameMode;
import com.arinax.entities.Post;
import com.arinax.entities.Role;
import com.arinax.entities.User;
import com.arinax.entities.Post.PostStatus;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.FullMapDto;
import com.arinax.playloads.FullMapResponse;
import com.arinax.playloads.PostDto;
import com.arinax.playloads.PostResponse;
import com.arinax.repositories.FullMapRepo;
import com.arinax.repositories.GameModeRepo;
import com.arinax.repositories.GameRepo;

import com.arinax.repositories.RoleRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.services.FullMapService;
import com.arinax.services.NotificationService;

@Service
public class FullMapServiceImpl implements FullMapService{

	 @Autowired
	    private FullMapRepo fullMapRepo;

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
		public FullMapDto createFullMap(FullMapDto fullMapDto, Integer userId, Integer gameId, Integer modeId) {
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


		       FullMap fulmap = this.modelMapper.map(fullMapDto, FullMap.class);
		        
		       fulmap.setImageName1("");
		       fulmap.setImageName2("");
		        
		       fulmap.setAddedDate(LocalDateTime.now());
		       fulmap.setUser(user);
		       fulmap.setGame(game);
		       fulmap.setGameMode(mode);
		       fulmap.setContent(fullMapDto.getContent());
		       fulmap.setEntryFee(fullMapDto.getEntryFee());
		        
		        LocalDateTime startTime = fullMapDto.getStartTime();

		        if (startTime == null) {
		            throw new ApiException("Start time cannot be null");
		        }

		        if (startTime.isBefore(LocalDateTime.now())) {
		            throw new ApiException("Start time must be in the future");
		        }

		        	fulmap.setStartTime(startTime);  
		   
		            fulmap.setStatus(FullMap.FullMapStatus.PENDING);
		          
		        //notification to all user 
		      FullMap newmap = this.fullMapRepo.save(fulmap);  
		        
		        return this.modelMapper.map(newmap, FullMapDto.class);
		    
		}

		@Override
		public FullMapDto updateFullMap(FullMapDto fullMapDto, Integer  fullmapId, Principal principal) {

	        FullMap map = this.fullMapRepo.findById( fullmapId)
	                .orElseThrow(() -> new ResourceNotFoundException("FullMap", "FullMap id", fullmapId));

	        Role adminRole = roleRepo.findById(AppConstants.ADMIN_USER)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role Id", AppConstants.ADMIN_USER));
	        
	        //List<User> allAdmins = userRepo.findAllByRoleName(adminRole.getName());
	        
	        if (map.getStatus() == FullMap.FullMapStatus.PRIVATE || map.getStatus() == FullMap.FullMapStatus.PLAYER_APPROVED) {
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


	        Game game = this.gameRepo.findById(fullMapDto.getGame().getGameId())
	                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", fullMapDto.getGame().getGameId()));

	        GameMode mode = this.modeRepo.findById(fullMapDto.getGameMode().getModeId())
	                .orElseThrow(() -> new ResourceNotFoundException("GameMode", "mode id", fullMapDto.getGameMode().getModeId()));
	        
	        if(fullMapDto.getTitle()!=null) {
	        	map.setTitle(fullMapDto.getTitle());
	        }
	        if(fullMapDto.getContent()!=null) {
	        	map.setContent(fullMapDto.getContent());
	        }
	        if(fullMapDto.getEntryFee()!=null) {
	        	map.setEntryFee(fullMapDto.getEntryFee());
	        }
	        if(fullMapDto.getImageName1()!=null) {
	        	map.setImageName1(fullMapDto.getImageName1());
	        }
	        if(fullMapDto.getImageName2()!=null) {
	        	map.setImageName2(fullMapDto.getImageName2());
	            }
	        map.setGame(game);
	        map.setGameMode(mode);
	        map.setStatus(FullMap.FullMapStatus.PENDING);
	            // Admin updated the post => confirm notification
//	            NotificationDto notificationDto = new NotificationDto();
//	       	 notificationDto.setMessage("Your post has been updated and approved successfully.");
//	           notificationService.createNotification(notificationDto, user.getId(),null);
	           
	       
	       FullMap updated = this.fullMapRepo.save(map);
	   
	        return this.modelMapper.map(updated, FullMapDto.class);

		}

		@Override
		public void deleteFullMap(Integer  fullmapId) {
			FullMap map = this.fullMapRepo.findById( fullmapId)
		                .orElseThrow(() -> new ResourceNotFoundException(" FullMap ", "FullMap id",  fullmapId));

		        this.fullMapRepo.delete(map);

		}

		@Override
		public FullMapResponse getAllFullMap(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
			 Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		        Pageable p = PageRequest.of(pageNumber, pageSize, sort);
		        
		        Page<FullMap> pageMap = this.fullMapRepo.findByStatus(FullMap.FullMapStatus.PENDING, p);


		        List<FullMap> allMaps = pageMap.getContent();

		        List<FullMapDto> mapDtos = allMaps.stream().map((map) -> this.modelMapper.map(map, FullMapDto.class))
		                .collect(Collectors.toList());

		        FullMapResponse MapResponse = new FullMapResponse();

		        MapResponse.setPageNumber(pageMap.getNumber());
		        MapResponse.setContent( mapDtos);
		        MapResponse.setPageSize(pageMap.getSize());
		       MapResponse.setTotalElements(pageMap.getTotalElements());

		        MapResponse.setTotalPages(pageMap.getTotalPages());
		        MapResponse.setLastPage(pageMap.isLast());

		        return MapResponse;
		}

		@Override
		public FullMapResponse getFullMapsByStatus(FullMapStatus status, Integer pageNumber, Integer pageSize,
				String sortBy, String sortDir) {
			 Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		        Pageable p= PageRequest.of(pageNumber, pageSize, sort);

		        Page<FullMap> pageMap = this.fullMapRepo.findByStatus(FullMap.FullMapStatus.PENDING, p);

			       // Page<Post> pagePost = this.postRepo.findAll(p);

			        List<FullMap> allMaps = pageMap.getContent();

			        List<FullMapDto> mapDtos = allMaps.stream().map((map) -> this.modelMapper.map(map, FullMapDto.class))
			                .collect(Collectors.toList());
		        FullMapResponse MapResponse = new FullMapResponse();

		        MapResponse.setPageNumber(pageMap.getNumber());
		        MapResponse.setContent( mapDtos);
		        MapResponse.setPageSize(pageMap.getSize());
		       MapResponse.setTotalElements(pageMap.getTotalElements());

		        MapResponse.setTotalPages(pageMap.getTotalPages());
		        MapResponse.setLastPage(pageMap.isLast());

		        return MapResponse;
		}

		
		@Override
		public FullMapDto getFullMapById(Integer  fullmapId) {
			FullMap fullMap = this.fullMapRepo.findById( fullmapId)
	                .orElseThrow(() -> new ResourceNotFoundException("FullMap", "FullMap id",  fullmapId));
	        return this.modelMapper.map(fullMap, FullMapDto.class);
		}

		@Override
		public List<FullMapDto> getFullMapsByUser(Integer userId) {
			 User user = this.userRepo.findById(userId)
		                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
		        List<FullMap> maps = this.fullMapRepo.findByUser(user);

		        List<FullMapDto> mapDtos = maps.stream().map((map) -> this.modelMapper.map(map, FullMapDto.class))
		                .collect(Collectors.toList());

		        return mapDtos;
		}

		@Override
		public List<FullMapDto> searchFullMaps(String keyword) {
			 List<FullMap> maps = this.fullMapRepo.searchByTitle("%" + keyword + "%");
		        List<FullMapDto> mapDtos = maps.stream().map((map) -> this.modelMapper.map(map, FullMapDto.class)).collect(Collectors.toList());
		        return mapDtos;
		}

		@Override
		public List<FullMapDto> getFullMapsByGame(Integer gameId) {
			 Game cat = this.gameRepo.findById(gameId)
		                .orElseThrow(() -> new ResourceNotFoundException("Game", "game id", gameId));
		        List<FullMap> maps = this.fullMapRepo.findByGame(cat);

		        List<FullMapDto> mapDtos = maps.stream().map((map) -> this.modelMapper.map(map, FullMapDto.class))
		                .collect(Collectors.toList());

		        return mapDtos;
		}

		@Override
		public FullMapDto rejectFullMap(Integer  fullmapId) {
			FullMap map = this.fullMapRepo.findById( fullmapId)
	                 .orElseThrow(() -> new ResourceNotFoundException("FullMap", "FullMap id",  fullmapId));
			
	        // Only allow approval if status is PENDING
	        if (map.getStatus() != FullMap.FullMapStatus.PENDING) { //pending hunai paro
	            throw new ApiException("Turnment Can only Reject In Panding Status");
	        }

	        map.setStatus(FullMap.FullMapStatus.REJECTED);
	       
//	        NotificationDto notificationDto = new NotificationDto();
//	    	 notificationDto.setMessage( "Your post titled '" + post.getTitle() + "' has been REJECTED.");
//	        notificationService.createNotification(notificationDto, post.getUser().getId(),null);

	        FullMap approvedmap = this.fullMapRepo.save(map);
	        return this.modelMapper.map(approvedmap, FullMapDto.class);
		}

		
		

		
}
