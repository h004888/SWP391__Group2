package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Chapters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChapterRepository extends JpaRepository<Chapters, Long> {
}
