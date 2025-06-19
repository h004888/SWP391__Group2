package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.List;

import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.OLearning.entity.User;
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
    private LessonService lessonService;

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

        if (!courses.isEmpty()) {
            model.addAttribute("course", courses.get(0)); // hoặc getFirst() nếu bạn dùng ListDeque
        } else {
            model.addAttribute("course", null); // hoặc ẩn phần này trên giao diện
        }

        return "userPage/index-3";
    }

    /**
     * Trích xuất đối tượng User từ Principal nếu có xác thực.
     */
    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }

    @GetMapping("/course/{courseId}")
    public String showCourseDetail(@PathVariable Long courseId, Principal principal, Model model,
            RedirectAttributes redirectAttributes) {
        User currentUser = extractCurrentUser(principal);
        if (currentUser == null)
            return "redirect:/login";

        Long userId = currentUser.getUserId();
        boolean isEnrolled = enrollmentService.hasEnrolled(userId, courseId);
        if (!isEnrolled) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa đăng ký khóa học này.");
            return "redirect:/home/coursesGrid";
        }

        Lesson currentLesson = lessonService.getCurrentLearningLesson(userId, courseId);
        // Chuyển hướng sang giao diện học bài
        return "redirect:/learning/lesson/view/" + currentLesson.getLessonId();
    }

    @GetMapping("/lesson/view/{lessonId}")
    public String viewLesson(@PathVariable Long lessonId, Principal principal, Model model) {
        User currentUser = extractCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Lesson lesson = lessonService.findLessonById(lessonId);
        Long courseId = lesson.getChapter().getCourse().getCourseId();
        Long userId = currentUser.getUserId();

        // Danh sách bài học theo chương
        List<Chapter> chapters = courseService.getChaptersWithLessons(courseId);

        // Tiến độ học
        int totalLessons = lessonService.countLessonsInCourse(courseId);
        int completedLessons = lessonCompletionService.countCompletedLessons(userId, courseId);
        int progress = (int) ((completedLessons * 100.0) / totalLessons);

        model.addAttribute("lesson", lesson);
        model.addAttribute("currentLessonId", lessonId);
        model.addAttribute("course", lesson.getChapter().getCourse());
        model.addAttribute("chapters", chapters);
        model.addAttribute("progress", progress);

        return "userPage/course-detail-min";
    }

}
