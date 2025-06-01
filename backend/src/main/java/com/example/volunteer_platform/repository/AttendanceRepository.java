package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEvent_EventIdAndVolunteer_UserId(Long eventId, Long volunteerId);
	List<Attendance> findByEvent_EventId(Long eventId);
}