package com.OLearning.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.OLearning.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentDTO {
    private Long reviewId;
    private Long courseId;
    private Long userId;
    private String comment;
    private Integer rating;
    private Long parentId;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> children;
}


