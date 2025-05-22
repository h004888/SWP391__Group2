package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepo extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourse_CourseID(int courseId);
}
