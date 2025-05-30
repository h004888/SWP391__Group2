package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonDTO toDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setLessonId((long) lesson.getLessonId());
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        dto.setContentType(lesson.getContentType());
        dto.setContent(lesson.getContent());
        dto.setDuration(lesson.getDuration());
        dto.setFree(lesson.isFree());
        if (lesson.getCourse() != null) {
            dto.setCourseId(lesson.getCourse().getCourseId());
            dto.setCourseTitle(lesson.getCourse().getTitle());
        }
        return dto;
    }

    public Lesson toEntity(LessonDTO dto, Course course) {
        Lesson lesson = new Lesson();
        lesson.setLessonId(dto.getLessonId());
        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        lesson.setContentType(dto.getContentType());
        lesson.setContent(dto.getContent());
        lesson.setDuration(dto.getDuration());
        lesson.setFree(dto.isFree());
        lesson.setCourse(course);
        return lesson;
    }
}
