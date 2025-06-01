package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.ProfileUpdateRequestDTO;
import com.example.volunteer_platform.dto.UserDTO;
import com.example.volunteer_platform.enums.RoleType;
import com.example.volunteer_platform.service.AuthService;
import com.example.volunteer_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    
    // Used to get the profile of user via Token / authentication
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        Long userId = userService.getUserByEmail(userEmail).getUserId();
        UserDTO userDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(userDTO);
    }

    
    // Used to search user's based on pincode, query etc and return list
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String query) {
        List<UserDTO> userDTOs = userService.searchUsers(pincode, query);
        return ResponseEntity.ok(userDTOs);
    }

    // here Organizer can search for a event's volunteer Like based on skills & pincode  ( With Role 'Organizer')
    @GetMapping("/organizer-search")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<List<UserDTO>> searchVolunteersForOrganizer(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String pincode) {
        List<UserDTO> userDTOs = userService.searchVolunteersForOrganizer(skills, pincode);
        return ResponseEntity.ok(userDTOs);
    }

    
    //Used to updated profile No matter in which role you are you can update your profile 
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateUserProfile(
            Authentication authentication,
            @ModelAttribute ProfileUpdateRequestDTO updateRequestDTO,
            @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) throws IOException {
        String userEmail = authentication.getName();
        UserDTO updatedProfile = userService.updateUserProfile(userEmail, updateRequestDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    // Used to get profile Like when we view the MyProfile 
    @GetMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        UserDTO userDTO = userService.getUserProfileByEmail(userEmail);
        return ResponseEntity.ok(userDTO);
    }
}