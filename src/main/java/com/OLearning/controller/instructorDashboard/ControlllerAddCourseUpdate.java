package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/instructordashboard")
public class ControlllerAddCourseUpdate {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ChapterService chapterService;

    @GetMapping()
    public String dashboard(Model model) {
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/content :: contentMain");

        return "instructorDashboard/indexUpdate";
    }
   //viewAllCourses
    @GetMapping("/courses")
    public String viewCourse(
            @RequestParam(name = "id", defaultValue = "2") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "7") int size,
            Model model, ModelMap modelMap) {
        Page<CourseDTO> coursePage = courseService.findCourseByUserId(id, page, size);
        modelMap.put("courses", coursePage.getContent());
        modelMap.put("userId", id);
        modelMap.put("currentPage", page);
        modelMap.put("totalPages", coursePage.getTotalPages());
        modelMap.put("totalElements", coursePage.getTotalElements());
        modelMap.put("size", size);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CoursesContent :: listsCourseContent");
        return "instructorDashboard/indexUpdate";
    }
}
