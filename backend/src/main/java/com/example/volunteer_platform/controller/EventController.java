// src/main/java/com/example/volunteer_platform/controller/EventController.java
package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.ApplicationDTO;
import com.example.volunteer_platform.dto.EventDTO;
import com.example.volunteer_platform.service.EventService;
import com.example.volunteer_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO, // Now expecting JSON payload
            Authentication authentication) throws IOException {
        String organizerEmail = authentication.getName();
        EventDTO createdEvent = eventService.createEvent(eventDTO, organizerEmail);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long eventId) {
        EventDTO eventDTO = eventService.getEventById(eventId);
        return ResponseEntity.ok(eventDTO);
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String query) {
        List<EventDTO> eventDTOs = eventService.getAllEvents(pincode, startDate, endDate, skills, query);
        return ResponseEntity.ok(eventDTOs);
    }

    @PostMapping("/{eventId}/apply")
    @PreAuthorize("hasAuthority('VOLUNTEER')")
    public ResponseEntity<ApplicationDTO> applyForEvent(@PathVariable Long eventId, Authentication authentication, @RequestBody(required = false) ApplicationDTO applicationDTO) {
        String volunteerEmail = authentication.getName();
        ApplicationDTO createdApplication = eventService.applyForEvent(eventId, volunteerEmail, applicationDTO);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/applicants")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<List<ApplicationDTO>> getApplicants(@PathVariable Long eventId) {
        List<ApplicationDTO> applicantDTOs = eventService.getApplicants(eventId);
        return ResponseEntity.ok(applicantDTOs);
    }

    @PutMapping("/{eventId}/applicants/{applicationId}/approve")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<ApplicationDTO> approveApplication(@PathVariable Long applicationId) {
        ApplicationDTO updatedApplication = eventService.approveApplication(applicationId);
        return ResponseEntity.ok(updatedApplication);
    }

    @PutMapping("/{eventId}/applicants/{applicationId}/reject")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<ApplicationDTO> rejectApplication(@PathVariable Long applicationId) {
        ApplicationDTO updatedApplication = eventService.rejectApplication(applicationId);
        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping("/applied")
    @PreAuthorize("hasAuthority('VOLUNTEER')")
    public ResponseEntity<List<EventDTO>> getAppliedEventsForVolunteer(Authentication authentication) {
        String volunteerEmail = authentication.getName();
        List<EventDTO> eventDTOs = eventService.getAppliedEventsForVolunteer(volunteerEmail);
        return ResponseEntity.ok(eventDTOs);
    }
}