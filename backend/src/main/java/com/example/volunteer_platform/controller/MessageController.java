package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.MessageDTO;
import com.example.volunteer_platform.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long messageId) {
        MessageDTO messageDTO = messageService.getMessageById(messageId);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/chat/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageDTO>> getMessagesForChat(@PathVariable Long chatId) {
        List<MessageDTO> messageDTOs = messageService.getMessagesForChat(chatId);
        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping("/send/{chatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long chatId,
            @RequestBody String content,
            Authentication authentication) {
        Long senderUserId = Long.parseLong(authentication.getName());

        MessageDTO savedMessage = messageService.saveMessage(chatId, senderUserId, content);
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }
}