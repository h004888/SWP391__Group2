package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
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
import com.OLearning.service.comment.CommentService;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.entity.CourseReview;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.service.notification.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.category.CategoryService;
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
    private CourseReviewRepository courseReviewRepository;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NotificationService notificationService;

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
        model.addAttribute("progress", lessonCompletionService.getOverallProgressOfUser(currentUser.getUserId(), courseViewDTO.getCourseId()));
        model.addAttribute("weeksEnrolled", enrollmentService.getWeeksEnrolled(currentUser.getUserId(), courseViewDTO.getCourseId()));
        model.addAttribute("numberOfCompletedLessons", lessonCompletionService.getNumberOfCompletedLessons(currentUser.getUserId(),
                courseViewDTO.getCourseId()));

        model.addAttribute("currentLesson",lessonService.getNextLessonAfterCompleted(currentUser.getUserId(),courseViewDTO.getCourseId()).get());

        // Add unread notification count
        long unreadCount = notificationService.countUnreadByUserId(currentUser.getUserId());
        model.addAttribute("unreadCount", unreadCount);

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
            if (quiz == null) {
                model.addAttribute("quizError", "Quiz not found for this lesson.");
                model.addAttribute("currentLesson", currentLesson);
                model.addAttribute("course", courseService.getCourseById(courseId));
                return "userPage/doQuiz";
            }
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
        for (Chapter chapter: course.getListOfChapters()){
            List<Lesson> lessons = chapter.getLessons();
            int totalLessons = lessons.size();
            int completedLessons = (int) lessons.stream().filter(lesson -> completedLessonIds.contains(lesson.getLessonId())).count();
            chapterProgressMap.put(chapter.getChapterId(),  new ChapterProgress(totalLessons, completedLessons));
        }

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        model.addAttribute("currentLessonId", currentLesson.getLessonId());
        model.addAttribute("currentLesson", currentLesson);

        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getListOfChapters());

        // Load comments for the specific lesson only (Rating = null)
        Course courseEntity = courseService.findCourseById(courseId);
        // Since we only support lesson-specific comments now, load comments for current lesson
        List<CourseReview> parentComments = courseReviewRepository.findByLessonAndRatingIsNullAndParentReviewIsNull(currentLesson);
        List<CommentDTO> comments = new ArrayList<>();

        for (CourseReview parentComment : parentComments) {
            CommentDTO commentDTO = commentMapper.toDTO(parentComment);
            List<CourseReview> replies = courseReviewRepository.findByParentReviewOrderByCreatedAtDesc(parentComment);
            List<CommentDTO> replyDTOs = replies.stream()
                    .map(reply -> commentMapper.toDTO(reply))
                    .collect(Collectors.toList());
            commentDTO.setChildren(replyDTOs);
            comments.add(commentDTO);
        }

        model.addAttribute("comments", comments);
        model.addAttribute("user", currentUser);

        // Add unread notification count
        long unreadCount = notificationService.countUnreadByUserId(currentUser.getUserId());
        model.addAttribute("unreadCount", unreadCount);

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

        CourseViewDTO course = courseService.getCourseById(courseId);
        boolean isInstructor = course != null && course.getInstructor() != null && course.getInstructor().getUserId().equals(user.getUserId());
        if (!isInstructor && !enrollmentService.hasEnrolled(user.getUserId(), courseId)) {
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
        boolean canAccess = isInstructor ||
                completedLessonIds.contains(lessonId) ||
                (nextAvailableLesson != null && nextAvailableLesson.getLessonId().equals(lessonId));

        if (!canAccess) {
            return "redirect:/learning/course/view?courseId=" + courseId;
        }

        Lesson nextLesson = lessonService.getNextLesson(courseId, lessonId);
        CourseViewDTO courseView = courseService.getCourseById(courseId);

        // Xác định các bài có thể truy cập
        Set<Long> accessibleLessonIds = new HashSet<>(completedLessonIds);
        if (nextAvailableLesson != null) {
            accessibleLessonIds.add(nextAvailableLesson.getLessonId());
        }
        if ("quiz".equalsIgnoreCase(currentLesson.getContentType())) {
            Quiz quiz = quizTestService.getQuizByLessonId(currentLesson.getLessonId());
            if (quiz == null) {
                model.addAttribute("quizError", "Quiz not found for this lesson.");
                model.addAttribute("currentLesson", currentLesson);
                model.addAttribute("course", courseView);
                return "userPage/doQuiz";
            }
            System.out.println("Quiz for quizID" + quiz.getId());
            List<QuizQuestion> questions = quizQuestionService.getQuestionsByQuizId(quiz.getId());

            model.addAttribute("quiz", quiz);
            model.addAttribute("currentLesson", currentLesson);
            model.addAttribute("course", courseView);

            model.addAttribute("questions", questions);
            QuizSubmissionForm submissionForm = new QuizSubmissionForm();
            submissionForm.setQuizId(quiz.getId());
            submissionForm.setCourseId(courseId);
            submissionForm.setLessonId(currentLesson.getLessonId());
            model.addAttribute("submissionForm", submissionForm);
            return "userPage/doQuiz";
        }


        Map<Long, ChapterProgress> chapterProgressMap = new HashMap<>();
        for (Chapter chapter: course.getListOfChapters()){
            List<Lesson> lessons = chapter.getLessons();
            int totalLessons = lessons.size();
            int completedLessons = (int) lessons.stream().filter(lesson -> completedLessonIds.contains(lesson.getLessonId())).count();
            chapterProgressMap.put(chapter.getChapterId(),  new ChapterProgress(totalLessons, completedLessons));
        }

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        // QUAN TRỌNG: currentLessonId bây giờ là bài đang được xem (lessonId từ URL)
        model.addAttribute("currentLessonId", lessonId); // Thay đổi từ currentLesson.getLessonId() thành lessonId
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("nextLesson", nextLesson);
        model.addAttribute("course", courseView);
        model.addAttribute("chapters", courseView.getListOfChapters());

        // Load comments for the specific lesson only (Rating = null)
        Course courseEntity = courseService.findCourseById(courseId);
        // Since we only support lesson-specific comments now, load comments for current lesson
        List<CourseReview> parentComments = courseReviewRepository.findByLessonAndRatingIsNullAndParentReviewIsNull(currentLesson);
        List<CommentDTO> comments = new ArrayList<>();

        for (CourseReview parentComment : parentComments) {
            CommentDTO commentDTO = commentMapper.toDTO(parentComment);
            List<CourseReview> replies = courseReviewRepository.findByParentReviewOrderByCreatedAtDesc(parentComment);
            List<CommentDTO> replyDTOs = replies.stream()
                    .map(reply -> commentMapper.toDTO(reply))
                    .collect(Collectors.toList());
            commentDTO.setChildren(replyDTOs);
            comments.add(commentDTO);
        }

        model.addAttribute("comments", comments);
        model.addAttribute("user", user);

        // Add unread notification count
        long unreadCount = notificationService.countUnreadByUserId(user.getUserId());
        model.addAttribute("unreadCount", unreadCount);

        return "userPage/course-detail-min";
    }

    @GetMapping("/course/{courseId}/public")
    public String showPublicCourseDetail(Principal principal, Model model, @PathVariable("courseId") Long courseId) {
        User currentUser = null;
        if (principal != null) {
            currentUser = extractCurrentUser(principal);
        }

        Course course = courseService.findCourseById(courseId);
        if (course == null) {
            return "redirect:/learning";
        }

        // Since we only support lesson-specific comments now, no comments to load for course-level
        List<CommentDTO> comments = new ArrayList<>();
        model.addAttribute("comments", comments);
        model.addAttribute("course", course);
        model.addAttribute("user", currentUser);
        model.addAttribute("currentLesson", null); // Đảm bảo fragment nhận biết là trang public
        // Add unread notification count if user is logged in
        if (currentUser != null) {
            long unreadCount = notificationService.countUnreadByUserId(currentUser.getUserId());
            model.addAttribute("unreadCount", unreadCount);
        }
        model.addAttribute("chapters", course.getListOfChapters());

        return "userPage/course-detail-min";
    }

//    @GetMapping("/course/view")
//    public String showCourseView(Principal principal, Model model, @RequestParam("courseId") Long courseId) {
//        User currentUser = null;
//        if (principal != null) {
//            currentUser = extractCurrentUser(principal);
//        }
//
//        // Kiểm tra xem user đã đăng ký khóa học chưa
//        boolean isEnrolled = false;
//        if (currentUser != null) {
//            isEnrolled = enrollmentService.hasEnrolled(currentUser.getUserId(), courseId);
//        }
//
//        if (isEnrolled) {
//            // Nếu đã đăng ký, redirect đến trang detail
//            return "redirect:/learning/course/" + courseId + "/detail";
//        } else {
//            // Nếu chưa đăng ký, redirect đến trang public
//            return "redirect:/learning/course/" + courseId + "/public";
//        }
//    }
}
