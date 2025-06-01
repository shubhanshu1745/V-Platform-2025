package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long eventId;
    private Long organizerId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String requiredSkills;
    private Integer volunteerCapacity;
    private Double paymentAmount;
    private String pincode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String photoBase64;
    private LocalDateTime lastRegistrationDate;
    private String contacts;
}