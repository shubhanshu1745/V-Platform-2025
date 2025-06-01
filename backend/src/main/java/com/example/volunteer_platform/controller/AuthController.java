package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.AuthRequestDTO;
import com.example.volunteer_platform.dto.OtpVerificationRequestDTO;
import com.example.volunteer_platform.dto.RoleSwitchRequest;
import com.example.volunteer_platform.dto.UserDTO;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.service.AuthService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // This method is for the registration Manage the API call for register -> authService(authreqDto)
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody AuthRequestDTO authRequestDTO) {
        UserDTO userDTO = authService.registerUser(authRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    
    // This method use to verify otp when user is try to register 
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequestDTO otpVerificationRequestDTO) {
        String token = authService.verifyOtp(otpVerificationRequestDTO);
        return ResponseEntity.ok(token);
    }

    // this method is used for Login via user email & password
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody AuthRequestDTO authRequestDTO) {
        String tokenResponse = authService.loginUser(authRequestDTO);
        return ResponseEntity.ok(tokenResponse);
    }

    
    // this method is used to switch the uesr role & also generate the new token Like the current role & User and return it.
    @PostMapping("/switch-role")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> switchRole(@RequestBody RoleSwitchRequest request, Authentication authentication) {
        ResponseEntity<Map<String, String>> tokenResponse = authService.switchRole(request.getRole());
        return tokenResponse;
    }
}