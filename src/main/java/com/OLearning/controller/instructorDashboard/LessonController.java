package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.entity.Course;
import com.OLearning.repository.instructorDashBoard.InstructorCourseRepo;
import com.OLearning.service.instructorDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/instructordashboard/viewcourse")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private InstructorCourseRepo instructorCourseRepo;

    // Hiển thị danh sách bài học theo courseId
    @GetMapping("/{courseId}/lessons")
    public String listLessons(@PathVariable Long courseId, Model model) {
        Course course = instructorCourseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<LessonDTO> lessons = lessonService.getLessonsByCourseId(courseId);

        model.addAttribute("course", course);
        model.addAttribute("lessons", lessons);
        model.addAttribute("courseId", courseId);

        return "instructorDashboard/list";
    }

    @GetMapping("/{courseId}/lessons/add")
    public String showAddForm(@PathVariable Long courseId, Model model) {
        LessonDTO lesson = new LessonDTO();
        lesson.setCourseId(courseId);
        model.addAttribute("lesson", lesson);
        return "instructorDashboard/form";
    }

    @PostMapping("/{courseId}/lessons/save")
    public String saveLesson(@PathVariable Long courseId, @ModelAttribute("lesson") LessonDTO dto) {
        dto.setCourseId(courseId);
        lessonService.saveLesson(dto);
        return "redirect:/instructordashboard/viewcourse/" + courseId + "/lessons";
    }

    @GetMapping("/{courseId}/lessons/edit/{lessonId}")
    public String editLesson(@PathVariable Long courseId, @PathVariable Long lessonId, Model model) {
        model.addAttribute("lesson", lessonService.getLessonById(lessonId));
        return "instructorDashboard/form";
    }

    @GetMapping("/{courseId}/lessons/delete/{lessonId}")
    public String deleteLesson(@PathVariable Long courseId, @PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return "redirect:/instructordashboard/viewcourse/" + courseId + "/lessons";
    }
}
