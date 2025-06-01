package com.example.volunteer_platform.dto;

import com.example.volunteer_platform.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long applicationId;
    private Long eventId;
    private Long volunteerId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}