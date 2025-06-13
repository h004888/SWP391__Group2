package com.OLearning.repository;

import com.OLearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    void deleteByLesson_LessonId(Long lessonId);
}
