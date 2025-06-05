package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
