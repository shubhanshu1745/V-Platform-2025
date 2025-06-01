package com.example.volunteer_platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String email;
    private String password;
    private String firstName; 
    private String lastName;  
    private String pincode;   
}