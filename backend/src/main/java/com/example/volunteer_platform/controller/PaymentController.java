package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.PaymentDTO;
import com.example.volunteer_platform.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForEvent(@PathVariable Long eventId) {
        List<PaymentDTO> payments = paymentService.getPaymentsForEvent(eventId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/volunteer/{volunteerId}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForVolunteer(@PathVariable Long volunteerId) {
        List<PaymentDTO> payments = paymentService.getPaymentsForVolunteer(volunteerId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/process/{eventId}/volunteer/{volunteerId}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<PaymentDTO> processPayment(@PathVariable Long eventId, @PathVariable Long volunteerId) {
        PaymentDTO paymentDTO = paymentService.processPayment(eventId, volunteerId);
        return ResponseEntity.ok(paymentDTO);
    }
}