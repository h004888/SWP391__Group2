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
        System.out.println("totalCourseIsPublish: " + courseService.countCourseIsPublish());
    }

}
