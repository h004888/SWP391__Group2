package com.OLearning.service.quizQuestion;

import java.util.List;

import org.springframework.stereotype.Service;

import com.OLearning.entity.QuizQuestion;

@Service
public interface QuizQuestionService {
    List<QuizQuestion> getQuestionsByQuizId(Long quizId);

}
