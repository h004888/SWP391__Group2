package com.OLearning.service.quizQuestion.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.entity.QuizQuestion;
import com.OLearning.repository.QuizQuestionRepository;
import com.OLearning.service.quizQuestion.QuizQuestionService;

@Service
public class QuizQuestionServiceImpl implements QuizQuestionService {
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Override
    public List<QuizQuestion> getQuestionsByQuizId(Long quizId) {
        return quizQuestionRepository.findAll().stream()
                .filter(q -> q.getQuiz().getId().equals(quizId))
                .collect(Collectors.toList());
    }

}
