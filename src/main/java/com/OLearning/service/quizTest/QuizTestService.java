package com.OLearning.service.quizTest;

import org.springframework.stereotype.Service;

import com.OLearning.entity.Quiz;
@Service
public interface QuizTestService {
    Quiz getQuizByLessonId(Long lessonId);
}
