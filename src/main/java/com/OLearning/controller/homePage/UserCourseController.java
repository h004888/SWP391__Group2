package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import com.OLearning.dto.chapter.ChapterProgress;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import com.OLearning.dto.quiz.QuizSubmissionForm;
import com.OLearning.entity.*;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;
import com.OLearning.service.quizQuestion.QuizQuestionService;
import com.OLearning.service.quizTest.QuizTestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.user.UserService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/learning")
public class UserCourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private LessonCompletionService lessonCompletionService;

    @Autowired
    private QuizTestService quizTestService;
    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UploadFile uploadFile;

    @GetMapping
    public String showUserDashboard(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = extractCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("categories", categoryService.getListCategories().stream().limit(5).toList());
        model.addAttribute("user", currentUser);
        List<Course> courses = enrollmentService.getCoursesByUserId(currentUser.getUserId());
        model.addAttribute("courses", courses); // nếu cần hiển thị list

        if (courses.isEmpty()) {
            model.addAttribute("course", null); // hoặc ẩn phần này trên giao diện

            return "userPage/LearningDashboard";
        }

        CourseViewDTO courseViewDTO = courseService.getCourseRecentIncomplete(currentUser.getUserId());

        model.addAttribute("course", courseViewDTO);
        model.addAttribute("progress",
                lessonCompletionService.getOverallProgressOfUser(currentUser.getUserId(), courseViewDTO.getCourseId()));
        model.addAttribute("weeksEnrolled",
                enrollmentService.getWeeksEnrolled(currentUser.getUserId(), courseViewDTO.getCourseId()));
        model.addAttribute("numberOfCompletedLessons",
                lessonCompletionService.getNumberOfCompletedLessons(currentUser.getUserId(),
                        courseViewDTO.getCourseId()));
        model.addAttribute("currentLesson",
                lessonService.getNextLessonAfterCompleted(currentUser.getUserId(), courseViewDTO.getCourseId()).get());
        model.addAttribute("courseByUser",
                courseService.getCourseByUserId(currentUser.getUserId()).stream()
                        .filter(co -> !co.getCourseId().equals(courseViewDTO.getCourseId()))
                        .collect(Collectors.toList()));
        model.addAttribute("progressCourses",
                enrollmentService.getProgressCoursesByUserId(currentUser.getUserId()).stream()
                        .filter(progress -> !progress.getCourseId().equals(courseViewDTO.getCourseId()))
                        .collect(Collectors.toList()));
        model.addAttribute("lsInstructor", userService.getUsersByRole(2L));
        model.addAttribute("totalEnrollments", courseViewDTO);
        return "userPage/LearningDashboard";
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

    @GetMapping("/course/view")
    public String showUserCourseDetail(Principal principal, Model model, @RequestParam("courseId") Long courseId) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = extractCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!enrollmentService.hasEnrolled(currentUser.getUserId(), courseId)) {
            return "redirect:/learning";
        }

        Lesson currentLesson;
        if (lessonCompletionService.getByUserAndCourse(currentUser.getUserId(), courseId).size() == 0) {
            currentLesson = lessonService.findFirstLesson(courseId);
        } else {
            currentLesson = lessonService.getNextLessonAfterCompleted(currentUser.getUserId(), courseId).orElse(null);
        }
        if (currentLesson == null) {
            currentLesson = lessonService.findFirstLesson(courseId);
        }

        List<Long> completedLessonIds = lessonCompletionService.getByUserAndCourse(currentUser.getUserId(), courseId)
                .stream()
                .map(LessonCompletionDTO::getLessonId)
                .collect(Collectors.toList());

        // Thêm logic để xác định bài có thể học được
        Set<Long> accessibleLessonIds = new HashSet<>(completedLessonIds);
        accessibleLessonIds.add(currentLesson.getLessonId()); // Bài hiện tại cũng có thể học
        if ("quiz".equalsIgnoreCase(currentLesson.getContentType())) {
            Quiz quiz = quizTestService.getQuizByLessonId(currentLesson.getLessonId());
            System.out.println("Quiz for quizID" + quiz.getId());
            List<QuizQuestion> questions = quizQuestionService.getQuestionsByQuizId(quiz.getId());

            model.addAttribute("quiz", quiz);
            model.addAttribute("currentLesson", currentLesson);
            model.addAttribute("course", courseService.getCourseById(courseId));

            model.addAttribute("questions", questions);
            QuizSubmissionForm submissionForm = new QuizSubmissionForm();
            submissionForm.setQuizId(quiz.getId());
            submissionForm.setCourseId(courseId);
            submissionForm.setLessonId(currentLesson.getLessonId());
            model.addAttribute("submissionForm", submissionForm);
            return "userPage/doQuiz";
        }
        CourseViewDTO course = courseService.getCourseById(courseId);

        Map<Long, ChapterProgress> chapterProgressMap = new HashMap<>();
        for (Chapter chapter : course.getListOfChapters()) {
            List<Lesson> lessons = chapter.getLessons();
            int totalLessons = lessons.size();
            int completedLessons = (int) lessons.stream()
                    .filter(lesson -> completedLessonIds.contains(lesson.getLessonId())).count();
            chapterProgressMap.put(chapter.getChapterId(), new ChapterProgress(totalLessons, completedLessons));
        }

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        model.addAttribute("currentLessonId", currentLesson.getLessonId());
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("lessonVideoURL", currentLesson.getVideo().getVideoUrl());

        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getListOfChapters());
        model.addAttribute("chapterProgressMap", chapterProgressMap);
        return "userPage/course-detail-min";
    }

    @GetMapping("course/{courseId}/lesson/{lessonId}")
    public String showUserLessonDetail(Principal principal, Model model, @PathVariable("lessonId") Long lessonId,
            @PathVariable("courseId") Long courseId) {
        User user = extractCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        // Thêm kiểm tra enrollment
        if (!enrollmentService.hasEnrolled(user.getUserId(), courseId)) {
            return "redirect:/learning";
        }

        Lesson currentLesson = lessonService.findLessonById(lessonId);
        if (currentLesson == null) {
            return "redirect:/learning/course/view?courseId=" + courseId;
        }

        // Kiểm tra xem user có quyền truy cập bài này không
        List<Long> completedLessonIds = lessonCompletionService.getByUserAndCourse(user.getUserId(), courseId).stream()
                .map(LessonCompletionDTO::getLessonId)
                .collect(Collectors.toList());

        Lesson nextAvailableLesson = lessonService.getNextLessonAfterCompleted(user.getUserId(), courseId).orElse(null);

        // Chỉ cho phép truy cập nếu bài đã hoàn thành hoặc là bài tiếp theo có thể học
        boolean canAccess = completedLessonIds.contains(lessonId) ||
                (nextAvailableLesson != null && nextAvailableLesson.getLessonId().equals(lessonId));

        if (!canAccess) {
            return "redirect:/learning/course/view?courseId=" + courseId;
        }

        Lesson nextLesson = lessonService.getNextLesson(courseId, lessonId);
        CourseViewDTO course = courseService.getCourseById(courseId);

        // Xác định các bài có thể truy cập
        Set<Long> accessibleLessonIds = new HashSet<>(completedLessonIds);
        if (nextAvailableLesson != null) {
            accessibleLessonIds.add(nextAvailableLesson.getLessonId());
        }
        if ("quiz".equalsIgnoreCase(currentLesson.getContentType())) {
            Quiz quiz = quizTestService.getQuizByLessonId(currentLesson.getLessonId());
            System.out.println("Quiz for quizID" + quiz.getId());
            List<QuizQuestion> questions = quizQuestionService.getQuestionsByQuizId(quiz.getId());

            model.addAttribute("quiz", quiz);
            model.addAttribute("currentLesson", currentLesson);
            model.addAttribute("course", course);

            model.addAttribute("questions", questions);
            QuizSubmissionForm submissionForm = new QuizSubmissionForm();
            submissionForm.setQuizId(quiz.getId());
            submissionForm.setCourseId(courseId);
            submissionForm.setLessonId(currentLesson.getLessonId());
            model.addAttribute("submissionForm", submissionForm);
            return "userPage/doQuiz";
        }

        Map<Long, ChapterProgress> chapterProgressMap = new HashMap<>();
        for (Chapter chapter : course.getListOfChapters()) {
            List<Lesson> lessons = chapter.getLessons();
            int totalLessons = lessons.size();
            int completedLessons = (int) lessons.stream()
                    .filter(lesson -> completedLessonIds.contains(lesson.getLessonId())).count();
            chapterProgressMap.put(chapter.getChapterId(), new ChapterProgress(totalLessons, completedLessons));
        }

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        // QUAN TRỌNG: currentLessonId bây giờ là bài đang được xem (lessonId từ URL)
        model.addAttribute("lessonVideoURL", currentLesson.getVideo().getVideoUrl());
        model.addAttribute("currentLessonId", lessonId);
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("nextLesson", nextLesson);
        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getListOfChapters());
        model.addAttribute("chapterProgressMap", chapterProgressMap);

        return "userPage/course-detail-min";
    }
}
