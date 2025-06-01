package com.example.volunteer_platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("OTP for Volunteer Platform Registration");
        message.setText("Your OTP is: " + otp + ". Please use this OTP to verify your email.");
        mailSender.send(message);
    }

    public void sendEventNotificationEmail(String toEmail, String eventTitle, String eventDescription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Volunteer Event Near You!");
        message.setText("A new event '" + eventTitle + "' has been created near your location!\n\n" +
                "Description: " + eventDescription + "\n\n" +
                "Check out the Volunteer Platform for more details and to apply.");
        mailSender.send(message);
    }
}