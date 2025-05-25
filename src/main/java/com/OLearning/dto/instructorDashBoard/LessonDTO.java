package com.OLearning.dto.instructorDashBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private Long lessonId;
    private String title;
    private String description;
    private String contentType;
    private String content;
    private int duration;
    private boolean isFree;
    private Long courseId;
    private String courseTitle; // Optional for display
}
