package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.repository.adminDashBoard.CourseRepo;
import com.OLearning.service.instructorDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instructor")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/")
    public String showDashboard() {
        return "instructorDashboard/index";
    }


    @GetMapping("/list")
    public String listLessons(Model model) {
        model.addAttribute("lessons", lessonService.getAllLessons());
        return "instructorDashboard/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("lesson", new LessonDTO());
        model.addAttribute("courses",lessonService.getAllLessons());
        return "instructorDashboard/form";
    }

    @PostMapping("/save")
    public String saveLesson(@ModelAttribute("lesson") LessonDTO dto) {
        lessonService.saveLesson(dto);
        return "redirect:/instructor/";
    }

    @GetMapping("/edit/{id}")
    public String editLesson(@PathVariable Long id, Model model) {
        model.addAttribute("lesson", lessonService.getLessonById(id));
        model.addAttribute("courses", lessonService.getAllLessons());
        return "instructorDashboard/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return "redirect:/instructor/";

    }
    
}
