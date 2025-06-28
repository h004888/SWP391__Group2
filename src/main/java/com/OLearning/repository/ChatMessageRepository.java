package com.OLearning.repository;

import com.OLearning.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.user.id = :userId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    void deleteBySessionId(String sessionId);
} 