package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.OLearning.entity.QuizQuestion;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

}
    