package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.MessageDTO;
import com.example.volunteer_platform.entity.Chat;
import com.example.volunteer_platform.entity.Message;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.ChatRepository;
import com.example.volunteer_platform.repository.MessageRepository;
import com.example.volunteer_platform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public MessageDTO getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        return modelMapper.map(message, MessageDTO.class);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesForChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        List<Message> messages = messageRepository.findByChat_ChatIdOrderBySentAtAsc(chatId);
        return messages.stream()
                .map(message -> modelMapper.map(message, MessageDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO saveMessage(Long chatId, Long senderUserId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + senderUserId));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);

        message = messageRepository.save(message);
        return modelMapper.map(message, MessageDTO.class);
    }
}