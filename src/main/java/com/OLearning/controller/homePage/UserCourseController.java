package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import com.OLearning.entity.*;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // Controller đã fix
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
            return "redirect:/learning"; // fallback nếu đã học hết
        }

        List<Long> completedLessonIds = lessonCompletionService.getByUserAndCourse(currentUser.getUserId(), courseId).stream()
                .map(LessonCompletionDTO::getLessonId)
                .collect(Collectors.toList());

        // Thêm logic để xác định bài có thể học được
        Set<Long> accessibleLessonIds = new HashSet<>(completedLessonIds);
        accessibleLessonIds.add(currentLesson.getLessonId()); // Bài hiện tại cũng có thể học

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        model.addAttribute("currentLessonId", currentLesson.getLessonId());
        model.addAttribute("currentLesson", currentLesson);
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getChapters());
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
        Course course = courseService.getCourseById(courseId);

        // Xác định các bài có thể truy cập
        Set<Long> accessibleLessonIds = new HashSet<>(completedLessonIds);
        if (nextAvailableLesson != null) {
            accessibleLessonIds.add(nextAvailableLesson.getLessonId());
        }

        model.addAttribute("completedLessonIds", completedLessonIds);
        model.addAttribute("accessibleLessonIds", accessibleLessonIds);
        // QUAN TRỌNG: currentLessonId bây giờ là bài đang được xem (lessonId từ URL)
        model.addAttribute("currentLessonId", lessonId); // Thay đổi từ currentLesson.getLessonId() thành lessonId
        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("nextLesson", nextLesson);
        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getChapters());
        return "userPage/course-detail-min";
    }
}
