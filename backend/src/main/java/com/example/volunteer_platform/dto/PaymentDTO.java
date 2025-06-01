package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private Long eventId;
    private Long volunteerId;
    private Double amount;
    private String status;
    private String transactionId; // Stripe PaymentIntent client secret (for frontend) or ID (backend)
}