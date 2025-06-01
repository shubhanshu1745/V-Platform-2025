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
public class AttendanceDTO {
    private Long attendanceId;
    private Long eventId;
    private Long volunteerId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}