package com.OLearning.service.chat;

import com.OLearning.dto.chat.ChatRequestDTO;
import com.OLearning.dto.chat.ChatResponseDTO;
import com.OLearning.entity.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatResponseDTO sendMessage(ChatRequestDTO request, Long userId);
    List<ChatMessage> getChatHistory(String sessionId);
    List<ChatMessage> getUserChatHistory(Long userId);
    void clearChatHistory(String sessionId);
} 