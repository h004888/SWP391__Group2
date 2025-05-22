package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepo extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse_CourseID(long courseId);
}
