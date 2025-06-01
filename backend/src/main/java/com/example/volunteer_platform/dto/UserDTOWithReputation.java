// Updated UserDTOWithReputation.java (DTO - Gamification Fields)
package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.enums.VolunteerLevel; // Import VolunteerLevel Enum <--- ADDED IMPORT

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTOWithReputation extends UserDTO {
    private Double reputationScore;

    // --- Gamification Features ---
    private Integer volunteerPoints; // Include gamification fields in UserDTOWithReputation as well
    private VolunteerLevel volunteerLevel; // Include VolunteerLevel Enum <--- ADDED
    // --- End Gamification Features ---
}