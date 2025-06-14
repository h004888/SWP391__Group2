package com.OLearning.service.quiz;

import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.entity.Quiz;

public interface QuizService {
    void deleteByLessonId(Long lessonId);
    Quiz findQuizByLessonId(Long lessonId);
    void deleteQuiz(Long quizId);
    Quiz saveQuiz(QuizDTO quizDTO, Long lessonId);
}
