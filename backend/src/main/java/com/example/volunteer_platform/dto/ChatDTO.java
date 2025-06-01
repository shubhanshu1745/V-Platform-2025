package com.example.volunteer_platform.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private Long chatId;
    private Long initiatorUserId;
    private Long participantUserId;
    private LocalDateTime startedAt;
}