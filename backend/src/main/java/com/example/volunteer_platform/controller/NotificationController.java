package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.NotificationDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notifications.send")
    @SendTo("/topic/public")
    public NotificationDTO sendPublicNotification(@Payload NotificationDTO notificationDTO) {
        return notificationDTO;
    }

    public void sendPrivateNotification(Long userId, NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/private",
                notificationDTO
        );
    }
}