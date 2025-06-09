package com.OLearning.mapper.lesson;

import com.OLearning.dto.lesson.LessonDTO;
import com.OLearning.entity.Lesson;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LessonMapper {
    public Lesson DtoToLesson(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDTO.getTitle());
        lesson.setDescription(lessonDTO.getDescription());
        lesson.setDuration(lessonDTO.getDuration());
        lesson.setIsFree(lessonDTO.getIsFree());
        lesson.setOrderNumber(lessonDTO.getOrderNumber());
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        return lesson;
    }
}
