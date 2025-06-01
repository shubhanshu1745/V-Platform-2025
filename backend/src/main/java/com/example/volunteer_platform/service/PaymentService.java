package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.PaymentDTO;
import com.example.volunteer_platform.entity.Event;
import com.example.volunteer_platform.entity.Payment;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.GeneralException;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.EventRepository;
import com.example.volunteer_platform.repository.PaymentRepository;
import com.example.volunteer_platform.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsForEvent(Long eventId) {
        List<Payment> payments = paymentRepository.findByEvent_EventId(eventId);
        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsForVolunteer(Long volunteerId) {
        List<Payment> payments = paymentRepository.findByVolunteer_UserId(volunteerId);
        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO processPayment(Long eventId, Long volunteerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));

        Double amount = event.getPaymentAmount();
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Event payment amount is invalid.");
        }

        try {
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("usd")
                    .setAmount((long) (amount * 100))
                    .putMetadata("event_id", String.valueOf(eventId))
                    .putMetadata("volunteer_id", String.valueOf(volunteerId))
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);

            Payment payment = new Payment();
            payment.setEvent(event);
            payment.setVolunteer(volunteer);
            payment.setAmount(amount);
            payment.setStatus("PENDING");
            payment.setTransactionId(paymentIntent.getId());

            payment = paymentRepository.save(payment);

            PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
            paymentDTO.setTransactionId(paymentIntent.getClientSecret());
            return paymentDTO;

        } catch (StripeException e) {
            logger.error("Error processing payment with Stripe for event {} and volunteer {}: {}", eventId, volunteerId, e.getMessage());
            throw new GeneralException("Error processing payment with Stripe: " + e.getMessage(), e);
        }
    }
}