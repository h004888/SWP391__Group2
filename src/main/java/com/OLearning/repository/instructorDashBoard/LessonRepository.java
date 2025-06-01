package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Lessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lessons, Long> {

}
