package com.OLearning.controller.instructorDashboard;


import com.OLearning.dto.instructorDashboard.*;
import com.OLearning.entity.Chapters;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lessons;
import com.OLearning.service.instructorDashBoard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InstructorController {
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
    //load all course by id of user login
    @GetMapping("/instructordashboard/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model, ModelMap modelMap) {
        List<CourseDTO> courseList = courseService.findCourseByUserId(id);
        modelMap.put("courses", courseList);
        return "instructorDashboard/course";
    }

    //dashboard
    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }


    //Create, countinue update with step, step 1
    @GetMapping("/instructordashboard/viewcourse/addcoursestep1")
    public String addNewCourse(@RequestParam(value="id", required = false) Long courseId,Model model) {

        if(courseId == null) {
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
    //NEXT step 2 and save step 1 or draf
    @PostMapping("/instructordashboard/viewcourse/addcoursestep2")
    public String SaveStep1NextToStep2(
            @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1, @RequestParam(name = "id", defaultValue = "2") Long id,
            @RequestParam(name="action") String action,  Model model,
            ModelMap modelMap) {

        if(action.equals("draft")) { //khi nay la draft khi o step 1
           Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
            courseStep1.setId(course.getCourseId());
            List<CourseDTO> courseList = courseService.findCourseByUserId(id);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
        courseStep1.setId(course.getCourseId());
        Long courseId = course.getCourseId();
        model.addAttribute("courseId", courseId);
        ChapterDTO chapterDTO = new ChapterDTO();
        chapterDTO.setCourseId(courseId);
        model.addAttribute("chapter", chapterDTO);
        List<Chapters> chapters = chapterService.chapterListByCourse(courseId);
        for(Chapters chapter : chapters) {
            List<Lessons> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if(lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("lessonDTO", new LessonDTO());
        return "instructorDashboard/CreateCourseStep2";
    }

//create chapter
    @Autowired
    private ChapterService chapterService;
    @PostMapping("/instructordashboard/viewcourse/chaptersadd")
    public String addNewChapter(@ModelAttribute("chapter") ChapterDTO chapterDTO,
                @RequestParam(name="courseId") Long courseId,
                Model model,
                RedirectAttributes redirectAttributes
    ) {
        chapterDTO.setCourseId(courseId);
        chapterService.saveChapter(chapterDTO);
        return "redirect:/instructordashboard/viewcourse/addcoursestep2?courseId=" + courseId;

    }
    //create lesson
    @PostMapping("/instructordashboard/viewcourse/lessonadd")
    public String addNewLesson(@ModelAttribute("lessonDTO") LessonDTO lessonDTO, RedirectAttributes redirectAttributes) {
        lessonService.createLesson(lessonDTO);
        Long chapterId = lessonDTO.getChapterId();
        Long courseId = chapterService.getChapterById(chapterId).getCourse().getCourseId();
        return "redirect:/instructordashboard/viewcourse/addcoursestep2?courseId=" + courseId;
    }
//up screen after add chapter, lesson step 2
    @GetMapping("/instructordashboard/viewcourse/addcoursestep2")
    public String showStep2Page(@RequestParam(name="courseId") Long courseId, Model model) {
        ChapterDTO chapterDTO = new ChapterDTO();
        chapterDTO.setCourseId(courseId);
        model.addAttribute("chapter", chapterDTO);

        List<Chapters> chapters = chapterService.chapterListByCourse(courseId);
        for(Chapters chapter : chapters) {
            List<Lessons> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if(lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("courseId", courseId);
        model.addAttribute("lessonDTO", new LessonDTO());
        return "instructorDashboard/CreateCourseStep2";
    }
//save step 2 and next step 3
    @PostMapping("/instructordashboard/viewcourse/addcoursestep3")
    public String step3(@RequestParam(name="courseId", required = false) Long courseId,
                        Model model, @RequestParam(name="action") String action,
    @RequestParam(name = "id", defaultValue = "2") Long userid, ModelMap modelMap) {
        if(action.equals("draft")) { //khi nay la draft khi o step 2
            Course course = courseService.createCourseStep2(courseId);
            List<CourseDTO> courseList = courseService.findCourseByUserId(userid);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        Long idtemp = courseId;
        Course course = courseService.createCourseStep2(courseId);
        model.addAttribute("course", course);

        return "instructorDashboard/CreateCourseStep3";
    }

//save all and submit
    @PostMapping("/instructordashboard/viewcourse/addcoursestep4")
    public String step4() {
        return "instructorDashboard/CreateCourseStep4";
    }

    //deleteCourse
    @PostMapping("/instructordashboard/viewcourse/deletecourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/instructordashboard/viewcourse";
    }
}
