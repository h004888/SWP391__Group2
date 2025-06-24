package com.OLearning.dto.lessonCompletion;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonCompletionDTO {
    private Integer completionId;
    private Long userId;
    private Long lessonId;
    private LocalDateTime completedAt;
}
