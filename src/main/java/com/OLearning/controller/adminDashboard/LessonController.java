package com.OLearning.controller.adminDashboard;

import com.OLearning.entity.Lesson;
import com.OLearning.service.adminDashBoard.CourseService;
import com.OLearning.service.adminDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listLessons(Model model) {
        List<Lesson> lessons = lessonService.getAllLessons();
        model.addAttribute("lessons", lessons);
        return "adminDashboard/lessons/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("courses", courseService.getAllCourses());
        return "adminDashboard/lessons/form";
    }

    @PostMapping("/save")
    public String saveLesson(@ModelAttribute Lesson lesson) {
        lessonService.saveLesson(lesson);
        return "redirect:/admin/lessons";
    }

    @GetMapping("/edit/{id}")
    public String editLesson(@PathVariable int id, Model model) {
        model.addAttribute("lesson", lessonService.getLessonById(id));
        model.addAttribute("courses", courseService.getAllCourses());
        return "adminDashboard/lessons/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteLesson(@PathVariable int id) {
        lessonService.deleteLesson(id);
        return "redirect:/admin/lessons";
    }
}
