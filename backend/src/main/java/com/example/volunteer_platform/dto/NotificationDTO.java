package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String messageType; // e.g., "eventNotification", "chatMessage"
    private String message;
    private Long userId; // Recipient user ID (for private notifications)
    private Long eventId; // Optional: Event ID for event-related notifications
    private String eventTitle; // Optional: Event title for context
    // Add more fields as needed for different notification types
}