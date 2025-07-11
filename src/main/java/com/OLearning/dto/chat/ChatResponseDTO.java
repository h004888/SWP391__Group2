package com.OLearning.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String message;
    private String sessionId;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;
} 