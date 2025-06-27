package com.OLearning.service.quiz.impl;

import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.dto.quiz.QuizQuestionDTO;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Quiz;
import com.OLearning.entity.QuizQuestion;
import com.OLearning.mapper.quiz.QuizMapper;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.QuizQuestionRepository;
import com.OLearning.repository.QuizRepository;
import com.OLearning.service.quiz.QuizService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl implements QuizService {
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private QuizMapper quizMapper;
    @Override
    public void deleteByLessonId(Long lessonId) {
        Quiz quiz = findQuizByLessonId(lessonId);
        if(quiz != null) {
            quizQuestionRepository.deleteByQuizId(quiz.getId());
            quizRepository.delete(quiz);
        }
    }

    @Override
    public Quiz findQuizByLessonId(Long lessonId) {
        Optional<Quiz> quiz = quizRepository.findByLesson_LessonId(lessonId);
        if(quiz.isPresent()) {
            return quiz.get();
        }
        return null;
    }


    @Override
    @Transactional
    public void deleteQuiz(Long quizId) {
        quizQuestionRepository.deleteByQuizId(quizId);
        quizRepository.deleteById(quizId);
    }

    @Override
    public Quiz saveQuiz(QuizDTO quizDTO, Long lessonId) {
        Quiz quiz;
        Optional<Quiz> existingQuiz = quizRepository.findByLesson_LessonId(lessonId);
        if (existingQuiz.isPresent()) {
            quiz = existingQuiz.get();
            quiz.setTitle(quizDTO.getTitle());
            quiz.setDescription(quizDTO.getDescription());
            quiz.setTimeLimit(quizDTO.getTimeLimit());
            quiz.setUpdatedAt(LocalDateTime.now());
            quiz.getQuestions().clear();
        } else {
            quiz = new Quiz();
            quiz.setTitle(quizDTO.getTitle());
            quiz.setDescription(quizDTO.getDescription());
            quiz.setTimeLimit(quizDTO.getTimeLimit());
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            quiz.setLesson(lesson);
            lesson.setContentType("quiz");
            lessonRepository.save(lesson);
        }

        // Add questions
        if (quizDTO.getQuestions() != null && !quizDTO.getQuestions().isEmpty()) {
            List<QuizQuestion> questions = new ArrayList<>();
            int orderNumber = 1;
            for (QuizQuestionDTO questionDTO : quizDTO.getQuestions()) {
                QuizQuestion question = new QuizQuestion();
                question.setQuestion(questionDTO.getQuestion());
                question.setOptionA(questionDTO.getOptionA());
                question.setOptionB(questionDTO.getOptionB());
                question.setOptionC(questionDTO.getOptionC());
                question.setOptionD(questionDTO.getOptionD());
                question.setCorrectAnswer(questionDTO.getCorrectAnswer());
                question.setOrderNumber(orderNumber++);
                question.setQuiz(quiz);
                questions.add(question);
            }

            quiz.getQuestions().addAll(questions);
        }

        return quizRepository.save(quiz);
    }
}
