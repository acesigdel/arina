package com.arinax.playloads;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arinax.entities.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseNotification {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public void notifyUser(User user, String message, Map<String, String> data) {
        if (user.getDeviceToken() == null) {
            throw new RuntimeException("Device token not found for user id: " + user.getId());
        } else {
            // Start building the message
            Message.Builder messageBuilder = Message.builder()
                .setToken(user.getDeviceToken())
                .setNotification(Notification.builder()
                    .setTitle("New Notification")
                    .setBody(message)
                    .build());

            // If data is not null, add it to the builder
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Now build the message
            Message fcmMessage = messageBuilder.build();

            try {
                String response = firebaseMessaging.send(fcmMessage);
                System.out.println("Push notification sent: " + response);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException("Failed to send notification", e);
            }
        }
    }
}
