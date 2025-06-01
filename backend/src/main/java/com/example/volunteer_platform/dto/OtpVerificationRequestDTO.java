package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationRequestDTO {
    private String email;
    private String otp;
}