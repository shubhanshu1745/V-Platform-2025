package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.ChatDTO;
import com.example.volunteer_platform.entity.Chat;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.ChatRepository;
import com.example.volunteer_platform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ChatDTO getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        return modelMapper.map(chat, ChatDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ChatDTO> getChatsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<Chat> chats = chatRepository.findByInitiatorOrParticipant(user, user);
        return chats.stream()
                .map(chat -> modelMapper.map(chat, ChatDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatDTO createChat(Long initiatorUserId, Long participantUserId) {
        User initiator = userRepository.findById(initiatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + initiatorUserId));
        User participant = userRepository.findById(participantUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + participantUserId));

        if (chatRepository.findByInitiatorAndParticipant(initiator, participant).isPresent() || chatRepository.findByInitiatorAndParticipant(participant, initiator).isPresent()) {
            throw new IllegalStateException("Chat already exists between these users.");
        }

        Chat chat = new Chat();
        chat.setInitiator(initiator);
        chat.setParticipant(participant);
        chat = chatRepository.save(chat);
        return modelMapper.map(chat, ChatDTO.class);
    }
}