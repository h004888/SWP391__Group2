package com.OLearning.service.quizTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.OLearning.entity.Quiz;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.quizQuestion.QuizQuestionService;
import com.OLearning.service.user.UserService;

@Component
public class ServiceTestRunner implements CommandLineRunner {

    @Autowired
    private QuizTestService quizTestService;
    @Autowired
    private QuizQuestionService quizQuestionService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private UserService userService;

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

        System.out.println("Course Service Test:" + courseService.getCourseByUserId(16L).get(0).getTitle());

        System.out.println(enrollmentService.getTotalEnrollmentOfInstructor(userService.getUsersByRole(2L)));
                

    }

}
