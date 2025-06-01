package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.EventDTO;
import com.example.volunteer_platform.dto.UserDTO;
import com.example.volunteer_platform.entity.Event;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.EventRepository;
import com.example.volunteer_platform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDTOWithPhoto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToUserDTOWithPhoto(user);
    }

    private UserDTO convertToUserDTOWithPhoto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPhotoBase64(encodePhotoToBase64(user.getPhoto()));
        return userDTO;
    }

    private String encodePhotoToBase64(byte[] photoBytes) {
        if (photoBytes != null && photoBytes.length > 0) {
            return java.util.Base64.getEncoder().encodeToString(photoBytes);
        }
        return null;
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return modelMapper.map(event, EventDTO.class);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (userDTO.getFirstName() != null) {
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            existingUser.setLastName(userDTO.getLastName());
        }
        if (userDTO.getCurrentRole() != null) {
            existingUser.setCurrentRole(userDTO.getCurrentRole());
        }
        if (userDTO.getPincode() != null) {
            existingUser.setPincode(userDTO.getPincode());
        }
        if (userDTO.getActivated() != null) {
            existingUser.setActivated(userDTO.getActivated());
        }

        existingUser = userRepository.save(existingUser);
        return convertToUserDTOWithPhoto(existingUser);
    }

    @Transactional
    public EventDTO updateEvent(Long eventId, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (eventDTO.getTitle() != null) {
            existingEvent.setTitle(eventDTO.getTitle());
        }
        if (eventDTO.getDescription() != null) {
            existingEvent.setDescription(eventDTO.getDescription());
        }
        if (eventDTO.getStartDate() != null) {
            existingEvent.setStartDate(eventDTO.getStartDate());
        }
        if (eventDTO.getEndDate() != null) {
            existingEvent.setEndDate(eventDTO.getEndDate());
        }
        if (eventDTO.getLocation() != null) {
            existingEvent.setLocation(eventDTO.getLocation());
        }
        if (eventDTO.getRequiredSkills() != null) {
            existingEvent.setRequiredSkills(eventDTO.getRequiredSkills());
        }
        if (eventDTO.getVolunteerCapacity() != null) {
            existingEvent.setVolunteerCapacity(eventDTO.getVolunteerCapacity());
        }
        if (eventDTO.getPaymentAmount() != null) {
            existingEvent.setPaymentAmount(eventDTO.getPaymentAmount());
        }
        if (eventDTO.getPincode() != null) {
            existingEvent.setPincode(eventDTO.getPincode());
        }

        existingEvent = eventRepository.save(existingEvent);
        return modelMapper.map(existingEvent, EventDTO.class);
    }
}