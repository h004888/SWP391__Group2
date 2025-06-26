package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Category;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Lesson;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.*;

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private NotificationService notificationService;
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

        model.addAttribute("accNamePage", "Management Course");
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

    //Filter with ajax
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

            List<Chapter> chapters = chapterService.chapterListByCourse(id);//tim list chapter theo courseID
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

}
