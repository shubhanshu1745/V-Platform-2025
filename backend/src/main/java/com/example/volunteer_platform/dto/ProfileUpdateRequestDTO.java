package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String pincode;
    private String contactDetails;
    private MultipartFile photoFile; // For photo upload
    private String gender;
    private String skills; // Skill-based profile feature
    private String availabilityCalendar; // Volunteer Availability Calendar
}