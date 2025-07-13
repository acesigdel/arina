package com.arinax.services;

import java.util.List;
import java.util.Map;

import com.arinax.entities.Notification;
import com.arinax.playloads.NotificationDto;

public interface NotificationService {
	
//void createNotification(Integer userId, String message);
//public NotificationDto createNotification(NotificationDto notificationDto,Integer userId);

List<Notification> getUnreadNotificationsForUser(Integer userId);



List<NotificationDto> getAllNotificationsForUser(Integer userId);
 void markNotificationsAsRead(Integer notificationId);

NotificationDto createNotification(NotificationDto notificationDto, Integer userId, Map<String, String> data);



void sendNotificationToUsers(List<Integer> userIds, String message, Map<String, String> data);

//NotificationDto createNotification(Integer userId, NotificationDto notificationDto);

//void notifyUser(User user, String message);


}
