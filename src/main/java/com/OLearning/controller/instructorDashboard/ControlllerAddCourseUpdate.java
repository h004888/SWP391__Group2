package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.User;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    //dashhboard
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
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CoursesContent :: listsCourseContent");
        return "instructorDashboard/indexUpdate";
    }

    //viewCourseDetail
    @GetMapping("/courses/detail/{courseId}")
    public String viewCourseDetail(@PathVariable("courseId") Long courseId, Model model) {
        Course course = courseService.findCourseById(courseId);
        if (course == null) {
            return "redirect:/courses";
        }
        User instructor = course.getInstructor();
        model.addAttribute("course", course);
        model.addAttribute("instructor", instructor);
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);//tim list chapter theo courseID
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("courseId", courseId);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/courseDetailContent :: courseDetailContent");
        return "instructorDashboard/indexUpdate";
    }

        //create Course Basic ìnformation
        //view course basic
        @GetMapping("/createcourse/coursebasic")
        public String addNewCourse(@RequestParam(name = "courseId", required = false) Long courseId, Model model) {
        if (courseId == null) {
            model.addAttribute("coursestep1", new AddCourseStep1DTO());//tao doi tương AddCourseStep1DTO de luu thong tin
            model.addAttribute("categories", categoryService.findAll());//in ra thong tin category
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }
        //khi nay la khi draft xong roi ma minh muon update lai course
        Course course = courseService.findCourseById(courseId); // tim course theo courseId
        AddCourseStep1DTO coursestep1 = courseService.draftCourseStep1(course);// chuyen doi course sang AddCourseStep1DT0
            // ---chuyen doi course da draft sang AddCourseStep1DTO
        model.addAttribute("coursestep1", coursestep1);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step1BasicInfor :: step1Content");
        return "instructorDashboard/indexUpdate";
    }
    //next to save course basic information
    @PostMapping("/createcourse/coursemedia")
    public String saveCourseBasic(@Valid @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1,
                                  BindingResult result,
                                  @RequestParam(name = "id", defaultValue = "2") Long id,
                                  @RequestParam(name = "action") String action,
                                  Model model,
                                  ModelMap modelMap) {
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(fieldError -> {
                if (fieldError.getCode().equals("typeMismatch") && fieldError.getField().equals("price")) {
                    model.addAttribute("priceTypeError", "Price must be a number");
                }
            });
            model.addAttribute("coursestep1", courseStep1);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }
        if (action.equalsIgnoreCase("draft")) {
            Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
            courseStep1.setId(course.getCourseId());
            return "redirect:../courses";
        }
        Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);//course=null thi se tao moi, con khong thi se update lai
        courseStep1.setId(course.getCourseId());//gan lai id cua course vao DTO
        Long courseId = course.getCourseId();
        model.addAttribute("courseId", courseId);
        model.addAttribute("coursestep2", new CourseMediaDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step2CourseMedia :: step2Content");
        return "instructorDashboard/indexUpdate";
    }
    //save course media and next to course content
    @PostMapping("/createcourse/coursecontent")
    public String saveCourseMedia(@Valid @ModelAttribute("coursestep2") CourseMediaDTO courseMediaDTO,
                                  BindingResult result,
                                  @RequestParam(name = "id", defaultValue = "2") Long id,
                                  Model model,
                                  @RequestParam(name = "action") String action) {
        if (result.hasErrors()) {
            model.addAttribute("coursestep2", courseMediaDTO);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step2CourseMedia :: step2Content");
            return "instructorDashboard/indexUpdate";
        }
        if (action.equalsIgnoreCase("draft")) {
            Course course = courseService.createCourseMedia(courseMediaDTO.getCourseId(), courseMediaDTO);
            return "redirect:../courses";
        }

        Course course = courseService.createCourseMedia(courseMediaDTO.getCourseId(), courseMediaDTO);
        model.addAttribute("courseId", course.getCourseId());
        model.addAttribute("coursestep3", new AddCourseStep1DTO());
       model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
        return "instructorDashboard/indexUpdate";
    }
}
