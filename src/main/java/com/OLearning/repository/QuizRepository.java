package com.OLearning.repository;

import com.OLearning.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Custom query methods can be defined here if needed
}
