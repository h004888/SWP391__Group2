package com.OLearning.service.quiz.impl;

import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Quiz;
import com.OLearning.mapper.quiz.QuizMapper;
import com.OLearning.repository.LessonRepository;
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
    @Autowired
    private LessonRepository lessonRepo;
    @Override
    public Quiz saveQuiz(QuizDTO quizDTO, Long lessonId) {
        Quiz quiz = mapper.convertToQuiz(quizDTO);
        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow();
        quiz.setLesson(lesson);
        return quizRepository.save(quiz);
    }
}
