package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.AttendanceDTO;
import com.example.volunteer_platform.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<AttendanceDTO> recordAttendance(@PathVariable Long eventId, @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO recordedAttendance = attendanceService.recordAttendance(attendanceDTO, eventId);
        return new ResponseEntity<>(recordedAttendance, HttpStatus.CREATED);
    }

    @GetMapping("/{volunteerId}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<AttendanceDTO> getAttendanceForVolunteer(
            @PathVariable Long eventId,
            @PathVariable Long volunteerId) {
        AttendanceDTO attendanceDTO = attendanceService.getAttendanceForVolunteer(eventId, volunteerId);
        return ResponseEntity.ok(attendanceDTO);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<List<AttendanceDTO>> getAllEventAttendance(@PathVariable Long eventId) {
        List<AttendanceDTO> attendanceDTOs = attendanceService.getAllEventAttendance(eventId);
        return ResponseEntity.ok(attendanceDTOs);
    }
}