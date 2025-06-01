package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.ChatDTO;
import com.example.volunteer_platform.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatDTO> getChatById(@PathVariable Long chatId) {
        ChatDTO chatDTO = chatService.getChatById(chatId);
        return ResponseEntity.ok(chatDTO);
    }
    

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatDTO>> getMyChats(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<ChatDTO> chatDTOs = chatService.getChatsForUser(userId);
        return ResponseEntity.ok(chatDTOs);
    }

    @PostMapping("/create/{participantUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatDTO> createChat(
            @PathVariable Long participantUserId,
            Authentication authentication) {
        // Long initiatorUserId = Long.parseLong(authentication.getName()); // <--- Example: Adjust this to get actual userId from Authentication
        Long initiatorUserId = Long.parseLong(authentication.getName()); // <--- Example: Adjust this to get actual userId from Authentication

        ChatDTO createdChat = chatService.createChat(initiatorUserId, participantUserId);
        return new ResponseEntity<>(createdChat, HttpStatus.CREATED);
    }
}