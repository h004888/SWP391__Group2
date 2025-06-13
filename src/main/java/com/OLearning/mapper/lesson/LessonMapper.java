package com.OLearning.mapper.lesson;

import com.OLearning.dto.lesson.LessonTitleDTO;
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
    public Lesson LessonTitleDtoToLesson(LessonTitleDTO lessonTitleDTO) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonTitleDTO.getTitle());
        lesson.setDescription(lessonTitleDTO.getDescription());
        lesson.setDuration(lessonTitleDTO.getDuration());
        lesson.setIsFree(lessonTitleDTO.getIsFree());
        lesson.setOrderNumber(lessonTitleDTO.getOrderNumber());
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        return lesson;
    }
}
