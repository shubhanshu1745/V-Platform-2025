package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEvent_EventId(Long eventId);
    List<Payment> findByVolunteer_UserId(Long volunteerId);
}