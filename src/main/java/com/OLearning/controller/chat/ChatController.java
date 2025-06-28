package com.OLearning.controller.chat;

import com.OLearning.dto.chat.ChatRequestDTO;
import com.OLearning.dto.chat.ChatResponseDTO;
import com.OLearning.entity.ChatMessage;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    /**
     * API endpoint để gửi tin nhắn (dùng cho chatbot widget)
     */
    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        try {
            // Lấy user ID từ authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                // Lấy user từ database bằng username
                User user = userRepository.findByUsername(authentication.getName()).orElse(null);
                if (user != null) {
                    userId = user.getUserId();
                }
            }
            
            ChatResponseDTO response = chatService.sendMessage(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChatResponseDTO errorResponse = new ChatResponseDTO(
                null,
                request.getSessionId(),
                java.time.LocalDateTime.now(),
                false,
                "Có lỗi xảy ra: " + e.getMessage()
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * API endpoint để lấy lịch sử chat theo session ID (dùng cho chatbot widget)
     */
    @GetMapping("/history/{sessionId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        try {
            List<ChatMessage> history = chatService.getChatHistory(sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API endpoint để xóa lịch sử chat theo session ID (dùng cho chatbot widget)
     */
    @DeleteMapping("/clear/{sessionId}")
    @ResponseBody
    public ResponseEntity<String> clearChatHistory(@PathVariable String sessionId) {
        try {
            chatService.clearChatHistory(sessionId);
            return ResponseEntity.ok("Chat history cleared successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error clearing chat history: " + e.getMessage());
        }
    }

    /**
     * API endpoint để lấy lịch sử chat của user đã đăng nhập (dùng cho chatbot widget)
     */
    @GetMapping("/user-history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getUserChatHistory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                User user = userRepository.findByUsername(authentication.getName()).orElse(null);
                if (user != null) {
                    userId = user.getUserId();
                    List<ChatMessage> history = chatService.getUserChatHistory(userId);
                    return ResponseEntity.ok(history);
                }
            }
            
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API endpoint để kiểm tra trạng thái chatbot
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Object> getChatbotStatus() {
        try {
            return ResponseEntity.ok(Map.of(
                "status", "online",
                "timestamp", java.time.LocalDateTime.now(),
                "version", "1.0.0"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }
} 