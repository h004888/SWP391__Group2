package com.OLearning.service.quizTest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.entity.Quiz;
import com.OLearning.repository.QuizTestRepository;
import com.OLearning.service.quizTest.QuizTestService;

@Service
public class QuizTestServiceImpl implements QuizTestService {

    @Autowired
    private QuizTestRepository quizTestRepository;

    @Override
    public Quiz getQuizByLessonId(Long lessonId) {
        return quizTestRepository.findAll().stream()
                .filter(quiz -> quiz.getLesson() != null && quiz.getLesson().getLessonId().equals(lessonId))
                .findFirst()
                .orElse(null);
    }

}
