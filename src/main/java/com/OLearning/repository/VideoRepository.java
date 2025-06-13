package com.OLearning.repository;

import com.OLearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    void deleteByLesson_LessonId(Long lessonId);
}
