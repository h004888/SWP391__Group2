package com.OLearning.service.quiz.impl;

import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.entity.Quiz;
import com.OLearning.mapper.quiz.QuizMapper;
import com.OLearning.repository.QuizRepository;
import com.OLearning.service.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository; // Assuming you have a QuizRepository for database operations
    @Autowired
    private QuizMapper mapper; // Assuming you have a Quiz entity to be used

    @Override
    public Quiz addQuiz(QuizDTO quizDTO) {
        if (quizDTO == null) {
            return null;
        }
        else {
            Quiz quiz = mapper.convertToQuiz(quizDTO);
            return quizRepository.save(quiz);
        }
    }
}
