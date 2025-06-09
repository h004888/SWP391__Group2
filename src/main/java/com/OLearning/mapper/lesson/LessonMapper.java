package com.OLearning.mapper.lesson;

import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.entity.Lesson;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LessonMapper {
    public Lesson DtoToLesson(LessonVideoDTO lessonVideoDTO) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonVideoDTO.getTitle());
        lesson.setDescription(lessonVideoDTO.getDescription());
        lesson.setDuration(lessonVideoDTO.getDuration());
        lesson.setIsFree(lessonVideoDTO.getIsFree());
        lesson.setOrderNumber(lessonVideoDTO.getOrderNumber());
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        return lesson;
    }
}
