package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.instructorDashboard.ChapterDTO;
import com.OLearning.dto.instructorDashboard.LessonDTO;
import com.OLearning.entity.Chapters;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lessons;
import com.OLearning.entity.User;
import com.OLearning.service.instructorDashBoard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/instructordashboard")
public class InstructorCourseDetailController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private InstructorBuyPackagesService buyPackagesService;
    @Autowired
    private PackagesService packagesService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ChapterService chapterService;
    @PostMapping("/courseDetail")
    public String viewCourseDetail(@RequestParam("courseId") Long courseId, Model model) {
        Course course = courseService.findCourseById(courseId);
        User instructor = course.getInstructor();
        model.addAttribute("course", course);
        model.addAttribute("instructor", instructor);
        List<Chapters> chapters = chapterService.chapterListByCourse(courseId);//tim list chapter theo courseID
        for (Chapters chapter : chapters) {
            List<Lessons> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("courseId", courseId);
        return "instructorDashboard/courseDetail";
    }
}
