package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.ApplicationDTO;
import com.example.volunteer_platform.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    @Autowired
    private EventService eventService;

    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<ApplicationDTO> approveApplication(@PathVariable Long applicationId) {
        ApplicationDTO updatedApplication = eventService.approveApplication(applicationId);
        return ResponseEntity.ok(updatedApplication);
    }

    @PutMapping("/{applicationId}/reject")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<ApplicationDTO> rejectApplication(@PathVariable Long applicationId) {
        ApplicationDTO updatedApplication = eventService.rejectApplication(applicationId);
        return ResponseEntity.ok(updatedApplication);
    }
}