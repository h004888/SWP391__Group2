package com.OLearning.service.quizTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.OLearning.entity.Quiz;
import com.OLearning.service.quizQuestion.QuizQuestionService;

@Component
public class ServiceTestRunner implements CommandLineRunner {

    @Autowired
    private QuizTestService quizTestService;
    @Autowired
    private QuizQuestionService quizQuestionService;

    @Override
    public void run(String... args) throws Exception {
        // You can use quizTestService here
        System.out.println("Running ServiceTestRunner...");
        Long lessonId = 1L; // Example lessonId
        Quiz quiz = quizTestService.getQuizByLessonId(lessonId);
        System.out.println("Quiz for lessonId " + quiz.getTitle());

        System.out.println("Quiz Questions:");
        System.out.println("---------------------------------");
        quizQuestionService.getQuestionsByQuizId(quiz.getId()).forEach(q -> {
            System.out.println("Câu hỏi: " + q.getQuestion());
            System.out.println("A: " + q.getOptionA());
            System.out.println("B: " + q.getOptionB());
            System.out.println("C: " + q.getOptionC());
            System.out.println("D: " + q.getOptionD());
            System.out.println("Đáp án đúng: " + q.getCorrectAnswer());
            System.out.println("-----");
        });

    }

}
