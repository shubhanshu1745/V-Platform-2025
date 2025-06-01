package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.AttendanceDTO;
import com.example.volunteer_platform.entity.Attendance;
import com.example.volunteer_platform.entity.Event;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.AttendanceRepository;
import com.example.volunteer_platform.repository.EventRepository;
import com.example.volunteer_platform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    private static final int POINTS_PER_EVENT_ATTENDANCE = 25;

    @Transactional
    public AttendanceDTO recordAttendance(AttendanceDTO attendanceDTO, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        User volunteer = userRepository.findById(attendanceDTO.getVolunteerId())
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + attendanceDTO.getVolunteerId()));

        Attendance attendance = modelMapper.map(attendanceDTO, Attendance.class);
        attendance.setEvent(event);
        attendance.setVolunteer(volunteer);
        attendance = attendanceRepository.save(attendance);

        if (attendanceDTO.getCheckOutTime() != null) {
            userService.awardPointsToVolunteer(volunteer.getUserId(), POINTS_PER_EVENT_ATTENDANCE);
            System.out.println("Awarded " + POINTS_PER_EVENT_ATTENDANCE + " points to volunteer " + volunteer.getUserId() + " for attending event " + eventId);
        }

        return modelMapper.map(attendance, AttendanceDTO.class);
    }

    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceForVolunteer(Long eventId, Long volunteerId) {
        Attendance attendance = attendanceRepository.findByEvent_EventIdAndVolunteer_UserId(eventId, volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found for event id: " + eventId + " and volunteer id: " + volunteerId));
        return modelMapper.map(attendance, AttendanceDTO.class);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAllEventAttendance(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        List<Attendance> attendanceList = attendanceRepository.findByEvent_EventId(eventId);
        return attendanceList.stream()
                .map(attendance -> modelMapper.map(attendance, AttendanceDTO.class))
                .collect(Collectors.toList());
    }
}