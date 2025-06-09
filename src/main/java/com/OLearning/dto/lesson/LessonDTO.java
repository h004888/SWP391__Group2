package com.OLearning.dto.lesson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private String title;
    private String description;
    private Integer orderNumber;
    private Integer duration;
    private Boolean isFree;
    private Long chapterId;
    private String videoUrl;
}
