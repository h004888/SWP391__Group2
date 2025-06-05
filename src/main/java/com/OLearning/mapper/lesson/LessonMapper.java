package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.LessonDTO;
import com.OLearning.entity.Lessons;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LessonMapper {
    public Lessons DtoToLesson(LessonDTO lessonDTO) {
        Lessons lesson = new Lessons();
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
