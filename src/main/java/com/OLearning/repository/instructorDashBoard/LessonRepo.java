package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LessonRepo extends JpaRepository<Lesson, Long> {

}
