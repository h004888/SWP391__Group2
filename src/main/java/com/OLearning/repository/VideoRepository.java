package com.OLearning.repository;

import com.OLearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    void deleteByLesson_LessonId(Long lessonId);
    Optional<Video> findByLesson_LessonId(Long lessonId);
}
