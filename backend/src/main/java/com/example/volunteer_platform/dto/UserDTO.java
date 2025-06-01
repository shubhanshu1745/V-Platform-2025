package com.example.volunteer_platform.dto;

import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.enums.VolunteerLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private RoleType currentRole;
    private String pincode;
    private Set<RoleType> roles; // Changed to Set<RoleType>
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean activated;
    private String contactDetails;
    private String photoBase64;
    private String gender;
    private String skills;
    private Integer volunteerPoints;
    private VolunteerLevel volunteerLevel;
    private String availabilityCalendar;
}