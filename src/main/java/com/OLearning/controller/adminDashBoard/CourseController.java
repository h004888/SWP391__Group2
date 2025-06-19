package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Category;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.notification.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private CourseReviewRepository courseReviewRepository;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String getCoursePage(Model model) {
        List<CourseDTO> listCourse = courseService.getAllCourses();
        List<Category> listCategories = categoryService.getListCategories();

        model.addAttribute("accNamePage", "Management Course");
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseContent :: courseContent");
        model.addAttribute("listCourse", listCourse);
        model.addAttribute("listCategories", listCategories);
        return "adminDashBoard/index";
    }

    //Filter with ajax
//    @GetMapping("/filter")
//    public String filterCourses(@RequestParam(required = false) String keyword,
//                                @RequestParam(required = false) Integer category,
//                                @RequestParam(required = false) String price,
//                                @RequestParam(required = false) String status,
//                                @RequestParam(defaultValue = "0") int page,
//                                @RequestParam(defaultValue = "5") int size,
//                                Model model) {
//
//        Page<CourseDTO> coursePage = courseService.filterCoursesWithPagination(
//                keyword, category, price, status, page, size);
//
//        model.addAttribute("listCourse", coursePage.getContent());
//
//        return "adminDashBoard/fragments/courseTableRowContent :: courseTableRowContent";
//    }
    @PostMapping("/approve/{id}")
    public String approveCourse(@PathVariable("id") Long id) {
        courseService.approveCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/reject")
    public String rejectCourse(@ModelAttribute("notification") NotificationDTO notificationDTO,
                               @RequestParam(name = "allowResubmission", defaultValue = "false") boolean allowResubmission,
                               RedirectAttributes redirectAttributes) {
        try {
            // Lấy user và course từ id (binding từ form)
            User user = userRepository.findById(notificationDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            Course course = courseRepository.findById(notificationDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
            notificationDTO.setUser(user);
            notificationDTO.setCourse(course);
            notificationService.rejectCourseMess(notificationDTO, allowResubmission);
            redirectAttributes.addFlashAttribute("successMessage", "Course rejected and notification sent successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting course: " + e.getMessage());
        }
        return "redirect:/admin/course";
    }
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/detail/{id}")
    public String viewCourseDetail(Model model, @PathVariable("id") Long id) {
        Long userId = id;
        Optional<CourseDetailDTO> optionalDetail = courseService.getDetailCourse(id);
        if (!optionalDetail.isPresent()) {
            return "redirect:/admin/course";
        }

        model.addAttribute("accNamePage", "Management Course");
        List<CourseDTO> listCourse = courseService.getAllCourses();
        List<Category> listCategories = categoryService.getListCategories();
        model.addAttribute("listCourse", listCourse);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("detailCourse", optionalDetail.get());
        
        // Lấy entity Course từ repository
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null) {
            model.addAttribute("reviews", courseReviewRepository.findByCourseWithUser(course));
        }
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseDetailContent :: courseDetail");
        return "adminDashBoard/index";
    }

    @PostMapping("/{courseId}/block")
    public String blockCourse(@PathVariable Long courseId, 
                              @RequestParam String reason,
                              Principal principal,
                              RedirectAttributes redirect) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
            // Khóa course bằng cách set status = "blocked"
            course.setStatus("blocked");
            courseRepository.save(course);
            
            // Gửi thông báo cho instructor
            User instructor = course.getInstructor();
            if (instructor != null) {
                Notification notification = new Notification();
                notification.setUser(instructor);
                notification.setCourse(course);
                notification.setMessage("Your course '" + course.getTitle() + "' has been blocked by admin. Reason: " + reason);
                notification.setType("COURSE_BLOCKED");
                notification.setStatus("failed");
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
            
            redirect.addFlashAttribute("success", "Course has been blocked successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Failed to block course: " + e.getMessage());
        }
        
        return "redirect:/admin/course/detail/" + courseId;
    }

    @PostMapping("/{courseId}/unblock")
    public String unblockCourse(@PathVariable Long courseId,
                                Principal principal,
                                RedirectAttributes redirect) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
            // Mở khóa course bằng cách set status = "approved"
            course.setStatus("approved");
            courseRepository.save(course);
            
            // Gửi thông báo cho instructor
            User instructor = course.getInstructor();
            if (instructor != null) {
                Notification notification = new Notification();
                notification.setUser(instructor);
                notification.setCourse(course);
                notification.setMessage("Your course '" + course.getTitle() + "' has been unblocked by admin.");
                notification.setType("COURSE_UNBLOCKED");
                notification.setStatus("failed");
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
            
            redirect.addFlashAttribute("success", "Course has been unblocked successfully");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Failed to unblock course: " + e.getMessage());
        }
        
        return "redirect:/admin/course/detail/" + courseId;
    }
}
