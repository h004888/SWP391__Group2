package com.OLearning.repository;

import java.util.List;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.OLearning.entity.LessonCompletion;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, Integer> {


        List<LessonCompletion> findByUser_UserIdAndLesson_Chapter_Course_CourseId(Long userId,
                                                                                  Long courseId);

        boolean existsByUserUserIdAndLessonLessonId(Long userId, Long lessonId);


}
