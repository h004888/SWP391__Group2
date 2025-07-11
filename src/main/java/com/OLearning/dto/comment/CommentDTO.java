package com.OLearning.dto.comment;

import com.OLearning.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long reviewId;
    private Long courseId;
    private Long lessonId;
    private Long userId;
    private String comment;
    private Integer rating;
    private Long parentId;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> children;
    private boolean hidden;
    private String replyToUserName;
}


