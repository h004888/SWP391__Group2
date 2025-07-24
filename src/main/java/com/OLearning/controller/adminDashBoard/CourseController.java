package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.*;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.email.EmailService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    private static final String ACC_NAME_PAGE_MANAGEMENT = "Management Course";
    private static final String SUCCESS_COURSE_REJECTED = "Course rejected and notification sent successfully.";
    private static final String ERROR_COURSE_REJECT = "Error rejecting course: ";

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @GetMapping
    public String getCoursePage(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String price) {

        Page<CourseDTO> pendingCourses = courseService.filterCoursesWithPagination(
                keyword, category, price, "pending", page, size);

        List<Category> listCategories = categoryService.getListCategories();

        model.addAttribute("accNamePage", ACC_NAME_PAGE_MANAGEMENT);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseContent :: courseContent");
        model.addAttribute("listCategories", listCategories);

        // Initial data for pending tab
        model.addAttribute("listCourse", pendingCourses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pendingCourses.getTotalPages());
        model.addAttribute("totalItems", pendingCourses.getTotalElements());
        model.addAttribute("pageSize", size);

        // Preserve filter parameters
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedPrice", price);

        return "adminDashBoard/index";
    }

    // Filter with ajax
    @GetMapping("/filter")
    public String filterCourses(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<CourseDTO> coursePage = courseService.filterCoursesWithPagination(
                keyword, category, price, status, page, size);

        model.addAttribute("listCourse", coursePage.getContent());

        return "adminDashBoard/fragments/courseTableRowContent :: courseTableRowContent";
    }

    // New endpoint for pagination only
    @GetMapping("/pagination")
    public String getPagination(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<CourseDTO> coursePage = courseService.filterCoursesWithPagination(
                keyword, category, price, status, page, size);

        model.addAttribute("listCourse", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalItems", coursePage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "adminDashBoard/fragments/courseTableRowContent :: coursePagination";
    }

    // New endpoint to get total count for badge
    @GetMapping("/count")
    @ResponseBody
    public long getCourseCount(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String status) {

        Page<CourseDTO> coursePage = courseService.filterCoursesWithPagination(
                keyword, category, price, status, 0, 1); // Get only 1 item to check total

        return coursePage.getTotalElements();
    }

    @GetMapping("/detail/{id}")
    public String viewCourseDetail(Model model, @PathVariable("id") Long id) {
        model.addAttribute("notification", new NotificationDTO());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseDetailContent :: courseDetail");
        Optional<CourseDetailDTO> optionalDetail = courseService.getDetailCourse(id);
        if (optionalDetail.isPresent()) {
            CourseDetailDTO courseDetail = optionalDetail.get();
            model.addAttribute("detailCourse", courseDetail);

            List<Chapter> chapters = chapterService.chapterListByCourse(id);// tim list chapter theo courseID
            for (Chapter chapter : chapters) {
                List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
                if (lessons != null && lessons.size() > 0) {
                    chapter.setLessons(lessons);
                }
            }
            if (chapters != null && !chapters.isEmpty()) {
                model.addAttribute("chapters", chapters);
            }

            return "adminDashBoard/index";
        } else {
            return "redirect:/admin/course";
        }
    }

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
            notificationService.rejectCourseMess(notificationDTO, allowResubmission);
            redirectAttributes.addFlashAttribute("successMessage", SUCCESS_COURSE_REJECTED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ERROR_COURSE_REJECT + e.getMessage());
        }
        return "redirect:/admin/course";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/{courseId}/block")
    public String blockCourse(@PathVariable Long courseId,
            @RequestParam String reason,
            Principal principal,
            RedirectAttributes redirect) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            // Chỉ cho phép block khi status là 'publish'
            if (!"publish".equalsIgnoreCase(course.getStatus())) {
                redirect.addFlashAttribute("error", "Chỉ có thể block khóa học khi đang ở trạng thái 'publish'.");
                return "redirect:/admin/course/detail/" + courseId;
            }
            // Chuyển trạng thái sang pending_block
            course.setStatus("pending_block");
            courseRepository.save(course);
            // KHÔNG block ngay, chỉ gửi email cho instructor
            User instructor = course.getInstructor();
            if (instructor != null) {
                // Gửi notification như cũ
                Notification notification = new Notification();
                notification.setUser(instructor);
                notification.setCourse(course);
                notification.setMessage(
                        "Your course '" + course.getTitle() + "' is under review for blocking by admin. Reason: "
                                + reason + ". Please log in to the system to respond.");
                notification.setType("COURSE_BLOCKED");
                notification.setStatus("failed");
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                // Gửi email cho instructor
                String subject = "[OLearning] Your course is under review for blocking";
                String content = "Xin chào " + instructor.getFullName() + ",\n\n" +
                        "Khóa học '" + course.getTitle() + "' của bạn đang bị xem xét khóa bởi quản trị viên.\n" +
                        "Lý do: " + reason + "\n" +
                        "Vui lòng đăng nhập vào hệ thống OLearning để phản hồi hoặc liên hệ bộ phận hỗ trợ nếu có thắc mắc.\n\n"
                        +
                        "Trân trọng,\nĐội ngũ OLearning";
                emailService.sendOTP(instructor.getEmail(), subject, content);
            }
            redirect.addFlashAttribute("success",
                    "Instructor has been notified to respond before blocking the course. Course status set to pending_block.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Failed to notify instructor: " + e.getMessage());
        }
        return "redirect:/admin/course/detail/" + courseId;
    }

    @PostMapping("/{courseId}/unblock")
    public String unblockCourse(@PathVariable Long courseId,
            Principal principal,
            RedirectAttributes redirect) {
        try {
            courseService.unblockCourse(courseId);
            // Gửi thông báo cho instructor
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
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

    @PostMapping("/{courseId}/block-confirm")
    public String blockCourse(@PathVariable("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        // Chỉ cho phép block khi status là 'pending_block' và trước đó phải là publish
        if (!"pending_block".equalsIgnoreCase(course.getStatus())) {
            redirectAttributes.addFlashAttribute("error",
                    "Chỉ có thể xác nhận block khi khóa học đang ở trạng thái 'pending_block'.");
            return "redirect:/admin/course/detail/" + courseId;
        }
        // Có thể kiểm tra thêm nếu cần
        courseService.blockCourse(courseId);
        redirectAttributes.addFlashAttribute("message", "Khóa học đã bị block!");
        return "redirect:/admin/course/detail/" + courseId;
    }

    @PostMapping("/{courseId}/block-cancel")
    public String cancelBlockCourse(@PathVariable("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus("publish");
        courseRepository.save(course);

        redirectAttributes.addFlashAttribute("message", "Đã hủy thao tác block.");
        return "redirect:/admin/course/detail/" + courseId;
    }

    @PostMapping("/{courseId}/pending-block")
    public String setPendingBlock(@PathVariable("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        courseService.setPendingBlock(courseId);
        redirectAttributes.addFlashAttribute("message", "Khóa học đã chuyển sang trạng thái chờ block!");
        return "redirect:/admin/course/detail/" + courseId;
    }
}
