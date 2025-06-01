package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.NotificationDTO;
import com.example.volunteer_platform.entity.Event;
import com.example.volunteer_platform.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendEventNotification(Event event, List<User> volunteers) {
        for (User volunteer : volunteers) {
            emailService.sendEventNotificationEmail(volunteer.getEmail(), event.getTitle(), event.getDescription());
            sendWebSocketNotification(volunteer, event);
        }
    }

    private void sendWebSocketNotification(User volunteer, Event event) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessageType("eventNotification");
        notificationDTO.setMessage("New event '" + event.getTitle() + "' created near you!");
        notificationDTO.setUserId(volunteer.getUserId());
        notificationDTO.setEventId(event.getEventId());
        notificationDTO.setEventTitle(event.getTitle());

        try {
            messagingTemplate.convertAndSendToUser(
                    volunteer.getUserId().toString(),
                    "/queue/notifications",
                    notificationDTO
            );
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification to user {}: {}", volunteer.getUserId(), e.getMessage());
            // Decide how to handle WebSocket notification failure: log, retry, etc.
            // For now, logging the error.
        }
    }
}