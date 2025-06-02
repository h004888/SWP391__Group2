package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.instructorDashboard.*;
import com.OLearning.entity.Course;
import com.OLearning.service.instructorDashboard.CourseService;
import com.OLearning.service.instructorDashboard.CategoryService;
import com.OLearning.service.instructorDashboard.InstructorBuyPackagesService;
import com.OLearning.service.instructorDashboard.PackagesService;
import com.OLearning.service.instructorDashboard.ChapterService;
import com.OLearning.mapper.instructorDashBoard.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Controller
public class InstructorController {
    @Autowired
    @Qualifier("instructorCourseService")
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private InstructorBuyPackagesService buyPackagesService;
    @Autowired
    private PackagesService packagesService;
    @Autowired
    @Qualifier("instructorCourseMapper")
    private CourseMapper courseMapper;

    // load all course by id of user login
    @GetMapping("/instructordashboard/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model, ModelMap modelMap) {
        List<CourseDTO> courseList = courseService.findCourseByUserId(id);
        modelMap.put("courses", courseList);
        return "instructorDashboard/course";
    }

    // dashboard
    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }

    // Create, countinue update with step, step 1
    @GetMapping("/instructordashboard/viewcourse/addcoursestep1")
    public String addNewCourse(@RequestParam(value = "id", required = false) Long courseId, Model model) {

        if (courseId == null) {
            model.addAttribute("coursestep1", new AddCourseStep1DTO());
            model.addAttribute("categories", categoryService.findAll());
            return "instructorDashboard/CreateCourseStep1";
        }
        Course course = courseService.findCourseById(courseId);
        AddCourseStep1DTO coursestep1 = courseService.draftCourseStep1(course);
        model.addAttribute("coursestep1", coursestep1);
        model.addAttribute("categories", categoryService.findAll());
        return "instructorDashboard/CreateCourseStep1";
    }

    // NEXT step 2 and save step 1 or draf
    @PostMapping("/instructordashboard/viewcourse/addcoursestep2")
    public String SaveStep1NextToStep2(
            @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1,
            @RequestParam(name = "id", defaultValue = "2") Long id, @RequestParam(name = "action") String action,
            Model model,
            ModelMap modelMap) {

        if (action.equals("draft")) { // khi nay la draft khi o step 1
            Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
            courseStep1.setId(course.getCourseId());
            List<CourseDTO> courseList = courseService.findCourseByUserId(id);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        // dung co save nua, save lan dau
        Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
        courseStep1.setId(course.getCourseId());
        Long courseId = course.getCourseId();
        // new code step 2
        ChapterDTO chapterDTO = new ChapterDTO();
        chapterDTO.setCourseId(courseId);
        model.addAttribute("chapter", chapterDTO);
        // -----------------------------------------
        return "instructorDashboard/CreateCourseStep2";
    }

    @Autowired
    private ChapterService chapterService;

    @PostMapping("/chapters/add")
    public String addNewChapter(@ModelAttribute("chapter") ChapterDTO chapterDTO) {
        chapterService.saveChapter(chapterDTO);
        return "instructorDashboard/CreateCourseStep1";
    }

    @PostMapping("/instructordashboard/viewcourse/addcourse/step3")
    public String step3() {
        return "instructorDashboard/CreateCourseStep3";
    }

    @PostMapping("/instructordashboard/viewcourse/addcourse/step4")
    public String step4() {
        return "instructorDashboard/CreateCourseStep4";
    }

    // deleteCourse
    @PostMapping("/instructordashboard/viewcourse/deletecourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/instructordashboard/viewcourse";
    }

    // update Course
    @GetMapping("/instructordashboard/viewcourse/updatecourse/{id}")
    public String updateCourseById(@PathVariable Long id, Model model) {
        // bay gio minh se fill course by id
        Course course = courseService.findCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("courseAddDTO", new CourseAddDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "instructorDashboard/updateCourse";
        // xong minh de gan no vao giao dien chinh qua cai Course
    }
}
