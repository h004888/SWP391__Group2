package com.OLearning.service.quiz;

import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.entity.Quiz;

public interface QuizService {
    Quiz addQuiz(QuizDTO quizDTO);
}
