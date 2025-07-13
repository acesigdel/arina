package com.arinax.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.config.AppConstants;
import com.arinax.entities.Notification;
import com.arinax.entities.Role;
import com.arinax.entities.User;
import com.arinax.exceptions.ApiException;
import com.arinax.exceptions.ResourceNotFoundException;
import com.arinax.playloads.FirebaseNotification;
import com.arinax.playloads.NotificationDto;
import com.arinax.repositories.NotificationRepo;
import com.arinax.repositories.RoleRepo;
import com.arinax.repositories.UserRepo;
import com.arinax.services.NotificationService;



@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FirebaseNotification firebaseNotification;
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
  //call from other class
    @Override
    public NotificationDto createNotification(NotificationDto notificationDto, Integer userId,Map<String, String> data) {
        if (notificationDto.getMessage() == null || notificationDto.getMessage().trim().isEmpty()) {
            throw new ApiException("Message cannot be empty");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(notificationDto.getMessage());

        try {
        	Map<String, String> pushData = (data == null) ? new HashMap<>() : data;
            firebaseNotification.notifyUser(user, notificationDto.getMessage(), pushData);
        } catch (RuntimeException e) {
            logger.warn("Push failed for user {}: {}", user.getId(), e.getMessage(), e);
            // Uncomment below if push failure should prevent saving
            // throw new ApiException("Push failed: " + e.getMessage());
        }

        Notification savedNotification = notificationRepo.save(notification);
        return modelMapper.map(savedNotification, NotificationDto.class);
    }

    
    
    @Override
    public void sendNotificationToUsers(List<Integer> userIds, String message, Map<String, String> data) {
        if (message == null || message.trim().isEmpty()) {
            throw new ApiException("Message cannot be empty");
        }

        CompletableFuture.runAsync(() -> {
            for (Integer userId : userIds) {
                try {
                    NotificationDto dto = new NotificationDto();
                    dto.setMessage(message);
                    createNotification(dto, userId, data);
                } catch (Exception e) {
                    logger.error("Failed to send notification to userId {}: {}", userId, e.getMessage(), e);
                }
            }
        });
    }
    @Override
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markNotificationsAsRead(Integer notificationId) {
    	
    	 Notification notification = notificationRepo.findById(notificationId)
                 .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        notification.setRead(true);
        notificationRepo.save(notification);
    }


   
    @Override
    public List<NotificationDto> getAllNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }



	
 

	
}