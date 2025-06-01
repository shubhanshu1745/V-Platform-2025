package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByEvent_EventId(Long eventId);
    Optional<Application> findByEvent_EventIdAndVolunteer_UserId(Long eventId, Long volunteerId);
    List<Application> findByVolunteer_UserId(Long volunteerId);
}