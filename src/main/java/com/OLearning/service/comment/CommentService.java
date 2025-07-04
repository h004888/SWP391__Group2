package com.OLearning.service.comment;

import com.OLearning.dto.comment.CommentDTO;

public interface CommentService {
    void addComment(CommentDTO dto, Long userId, Long courseId);
    void replyComment(CommentDTO dto, Long userId, Long courseId);
    void editComment(CommentDTO dto, Long userId, Long courseId);
    void deleteComment(Long commentId, Long userId, Long courseId);
}

