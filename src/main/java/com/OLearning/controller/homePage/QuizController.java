package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.OLearning.entity.Lesson;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 

import com.OLearning.dto.quiz.QuizSubmissionForm;
import com.OLearning.entity.Quiz;
import com.OLearning.entity.QuizQuestion;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.lessonCompletion.LessonCompletionService;
import com.OLearning.service.quizQuestion.QuizQuestionService;
import com.OLearning.service.quizTest.QuizTestService;

@Controller
public class QuizController {

    @Autowired
    private QuizTestService quizTestService;
    @Autowired
    private QuizQuestionService quizQuestionService;
    @Autowired
    private LessonCompletionService lessonCompletionService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private EnrollmentService enrollmentService;
    @PostMapping("quiz/submit")
    public String handleQuizSubmit(@ModelAttribute("submissionForm") QuizSubmissionForm form, Model model,
            Principal principal) {

        User user = extractCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Map<Integer, String> answers = form.getAnswers();
        Quiz quiz = quizTestService.getQuizByLessonId(form.getLessonId());
        List<QuizQuestion> questions = quizQuestionService.getQuestionsByQuizId(form.getQuizId());
        System.out.println("quizId: " + form.getQuizId());
        System.out.println("lessonId: " + form.getLessonId());
        System.out.println("courseId: " + form.getCourseId());
        System.out.println("answers: " + answers);
        System.out.println("questions: " + questions);

        int total = questions.size();
        int correct = 0;

        for (QuizQuestion q : questions) {
            String submitted = answers.get(q.getId().intValue());
            if (submitted != null && submitted.equalsIgnoreCase(q.getCorrectAnswer())) {
                correct++;
            }
        }

        double scorePercent = ((double) correct / total) * 100;
        boolean isPassed = scorePercent >= 75;
        if (isPassed) {
          if (!lessonCompletionService.checkLessonCompletion(user.getUserId(), form.getLessonId())) {
              lessonCompletionService.markLessonAsCompleted(user.getUserId(), form.getLessonId());
              enrollmentService.updateProgressByUser(user.getUserId(), form.getCourseId());
          }
        }

        Lesson nextLesson = lessonService.getNextLessonAfterCurrent(form.getLessonId(), form.getCourseId());
        Lesson lessonToShow = nextLesson != null ? nextLesson : lessonService.findFirstLesson(form.getCourseId());
        model.addAttribute("nextLessonId", lessonToShow != null ? lessonToShow.getLessonId() : null);




        model.addAttribute("courseId", form.getCourseId());
        model.addAttribute("score", correct);

        model.addAttribute("total", total);
        model.addAttribute("passed", isPassed); // true nếu >= 75%
        model.addAttribute("lessonId", form.getLessonId());
        model.addAttribute("result", true);

        return "userPage/doQuiz";

    }

    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }

}
