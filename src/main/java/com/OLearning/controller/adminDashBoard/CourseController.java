package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.dto.adminDashBoard.NotificationDTO;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.entity.Notifications;
import com.OLearning.entity.User;
import com.OLearning.repository.adminDashBoard.CourseRepository;
import com.OLearning.repository.adminDashBoard.NotificationRepository;
import com.OLearning.repository.adminDashBoard.UserRepository;
import com.OLearning.service.adminDashBoard.CategoriesService;
import com.OLearning.service.adminDashBoard.CourseService;
import com.OLearning.service.adminDashBoard.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoriesService categoriesService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public String getCoursePage(Model model) {
        List<CourseDTO> listCourse = courseService.getAllCourses();
        List<Categories> listCategories = categoriesService.getListCategories();

        model.addAttribute("accNamePage", "Management Course");
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseContent :: courseContent");
        model.addAttribute("listCourse", listCourse);
        model.addAttribute("listCategories", listCategories);

        return "adminDashBoard/index";
    }

    //Filter with ajax
    @GetMapping("/filter")
    public String filterCourses(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer category,
                                @RequestParam(required = false) String price,
                                @RequestParam(required = false) String status,
                                Model model) {
        List<CourseDTO> listCourse = courseService.filterCourses(keyword, category, price, status);
        model.addAttribute("listCourse", listCourse);
        return "adminDashBoard/fragments/courseContent :: courseTableBody";
    }

    @PostMapping("/approve/{id}")
    public String approveCourse(@PathVariable("id") Long id) {
        courseService.approveCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/violate/{id}")
    public String rejectCourse(@PathVariable("id") Long id) {
        courseService.violateCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/reject")
    public String rejectCourse(@ModelAttribute("notification") NotificationDTO notificationDTO,
                               @RequestParam(name = "allowResubmission", defaultValue = "false") boolean allowResubmission,
                               RedirectAttributes redirectAttributes) {
        try {
            notificationService.rejectCourseMess(notificationDTO, allowResubmission);
            redirectAttributes.addFlashAttribute("successMessage", "Course rejected and notification sent.");
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
        model.addAttribute("notification", new NotificationDTO());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/courseDetailContent :: courseDetail");
        Optional<CourseDetailDTO> optionalDetail = courseService.getDetailCourse(id);
        if (optionalDetail.isPresent()) {
            model.addAttribute("detailCourse", optionalDetail.get());
            return "adminDashBoard/index";
        } else {
            return "redirect:/admin/course";
        }

    }
}
