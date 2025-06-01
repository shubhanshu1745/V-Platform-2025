// src/main/java/com/example/volunteer_platform/service/EventService.java
package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.ApplicationDTO;
import com.example.volunteer_platform.dto.EventDTO;
import com.example.volunteer_platform.entity.Application;
import com.example.volunteer_platform.entity.Event;
import com.example.volunteer_platform.entity.Pincode;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.enums.ApplicationStatus;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.ApplicationRepository;
import com.example.volunteer_platform.repository.EventRepository;
import com.example.volunteer_platform.repository.PincodeRepository;
import com.example.volunteer_platform.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private PincodeRepository pincodeRepository;

 // src/main/java/com/example/volunteer_platform/service/EventService.java
    @Transactional
    public EventDTO createEvent(EventDTO eventDto, String organizerEmail) throws IOException {
        User organizer = userService.getUserByEmail(organizerEmail);
        if (organizer == null) {
            throw new ResourceNotFoundException("Organizer not found with email: " + organizerEmail);
        }

        Optional<Pincode> pincodeOptional = pincodeService.getPincodeByPincodeValue(eventDto.getPincode());
        if (!pincodeOptional.isPresent()) {
            throw new ResourceNotFoundException("Pincode not found: " + eventDto.getPincode());
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.setOrganizer(organizer);
        event.setPincode(eventDto.getPincode());

        String base64String = eventDto.getPhotoBase64(); // your incoming string
        if (base64String.contains(",")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }
        byte[] photoBytes = Base64.getDecoder().decode(base64String);
        event.setPhoto(photoBytes);

        event = eventRepository.save(event);
        logger.info("createEvent: Event saved to database with ID: {}", event.getEventId());

        List<User> nearbyVolunteers = pincodeService.findNearbyVolunteers(eventDto.getPincode(), 50.0);
        notificationService.sendEventNotification(event, nearbyVolunteers);

        return convertToEventDTOWithPhoto(event);
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return convertToEventDTOWithPhoto(event);
    }

    private EventDTO convertToEventDTOWithPhoto(Event event) {
        EventDTO eventDTO = modelMapper.map(event, EventDTO.class);
        eventDTO.setPhotoBase64(encodePhotoToBase64(event.getPhoto()));
        return eventDTO;
    }

    private String encodePhotoToBase64(byte[] photoBytes) {
        if (photoBytes != null && photoBytes.length > 0) {
            return Base64.getEncoder().encodeToString(photoBytes);
        }
        return null;
    }


    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents(String pincode, LocalDateTime startDate, LocalDateTime endDate, String skillsFilter, String query) {
        Specification<Event> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (pincode != null && !pincode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("pincode"), pincode));
            }
            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("startDate"), startDate, endDate));
            }
            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + query.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + query.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("requiredSkills")), "%" + query.toLowerCase() + "%")
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Event> events = eventRepository.findAll(spec);

        if (skillsFilter != null && !skillsFilter.isEmpty()) {
            String[] volunteerSkills = skillsFilter.toLowerCase().split(",");
            events = events.stream()
                    .filter(event -> eventHasMatchingSkill(event, volunteerSkills))
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(this::convertToEventDTOWithPhoto)
                .collect(Collectors.toList());
    }

    private boolean eventHasMatchingSkill(Event event, String[] volunteerSkills) {
        if (event.getRequiredSkills() == null || event.getRequiredSkills().isEmpty() || volunteerSkills == null || volunteerSkills.length == 0) {
            return true;
        }

        String[] eventRequiredSkills = event.getRequiredSkills().toLowerCase().split(",");

        for (String volunteerSkill : volunteerSkills) {
            for (String requiredSkill : eventRequiredSkills) {
                if (requiredSkill.trim().contains(volunteerSkill.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public ApplicationDTO applyForEvent(Long eventId, String volunteerEmail, ApplicationDTO applicationDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        User volunteer = userService.getUserByEmail(volunteerEmail);
        if (volunteer == null) {
            throw new ResourceNotFoundException("Volunteer not found with email: " + volunteerEmail);
        }

        if (applicationRepository.findByEvent_EventIdAndVolunteer_UserId(eventId, volunteer.getUserId()).isPresent()) {
            throw new IllegalStateException("Volunteer has already applied for this event.");
        }

        Application application = new Application();
        application.setEvent(event);
        application.setVolunteer(volunteer);

        application = applicationRepository.save(application);

        return modelMapper.map(application, ApplicationDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicants(Long eventId) {
        List<Application> applications = applicationRepository.findByEvent_EventId(eventId);
        return applications.stream()
                .map(application -> modelMapper.map(application, ApplicationDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDTO approveApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        application.setStatus(ApplicationStatus.APPROVED);
        application = applicationRepository.save(application);
        return modelMapper.map(application, ApplicationDTO.class);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getAppliedEventsForVolunteer(String volunteerEmail) {
        User volunteer = userService.getUserByEmail(volunteerEmail);
        if (volunteer == null) {
            throw new ResourceNotFoundException("Volunteer not found with email: " + volunteerEmail);
        }

        List<Application> applications = applicationRepository.findByVolunteer_UserId(volunteer.getUserId());

        return applications.stream()
                .map(application -> convertToEventDTOWithPhoto(application.getEvent()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDTO rejectApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        application.setStatus(ApplicationStatus.REJECTED);
        application = applicationRepository.save(application);
        return modelMapper.map(application, ApplicationDTO.class);
    }
}