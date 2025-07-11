package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Quiz;
import java.util.List;
import com.OLearning.entity.Lesson;

@Repository
public interface QuizTestRepository extends JpaRepository<Quiz, Long> {
    

}
