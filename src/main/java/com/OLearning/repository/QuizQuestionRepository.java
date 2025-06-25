package com.OLearning.repository;

import com.OLearning.dto.quiz.QuizQuestionDTO;
import com.OLearning.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizIdOrderByOrderNumber(Long quizId);
    void deleteByQuizId(Long quizId);
}
