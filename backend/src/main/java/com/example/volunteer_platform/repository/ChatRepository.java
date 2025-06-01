package com.example.volunteer_platform.repository;

import com.example.volunteer_platform.entity.Chat;
import com.example.volunteer_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByInitiatorOrParticipant(User initiator, User participant);
    Optional<Chat> findByInitiatorAndParticipant(User initiator, User participant);
}