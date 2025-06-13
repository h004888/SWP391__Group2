package com.OLearning.controller;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.AddCourseStep3DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
//@RequestMapping("/instructordashboard")
public class InstructorAddCourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ChapterService chapterService;

    //load all course by id of user login
    @GetMapping("/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model, ModelMap modelMap) {
        List<CourseDTO> courseList = courseService.findCourseByUserId(id);
        modelMap.put("courses", courseList);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/listCourseContent :: listsCourseContent");
        return "instructorDashboard/index";
    }

    //dashboard
    @GetMapping()
    public String dashboard(Model model) {
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/content :: contentMain");

        return "instructorDashboard/index";
    }

    //Create, countinue update with step, step 1
    @GetMapping("/viewcourse/addcoursestep1")
    public String addNewCourse(@RequestParam(name = "courseId", required = false) Long courseId, Model model) {

//        if (courseId == null) {
//            model.addAttribute("coursestep1", new AddCourseStep1DTO());
//            model.addAttribute("categories", categoryService.findAll());
//            model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep1Content :: step1Content");
//            return "instructorDashboard/indexUpdate";
////            return "instructorDashboard/CreateCourseStep1";
//        }
//        Course course = courseService.findCourseById(courseId);
//        AddCourseStep1DTO coursestep1 = courseService.draftCourseStep1(course);
//        model.addAttribute("coursestep1", coursestep1);
//        model.addAttribute("categories", categoryService.findAll());

//        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep1Content :: step1Content");
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseContent :: CreateCourseContent");
        return "instructorDashboard/indexUpdate";
//        return "instructorDashboard/CreateCourseStep1";
    }

    //NEXT step 2 and save step 1 or draf
    @PostMapping("/viewcourse/addcoursestep2")
    public String SaveStep1NextToStep2(
            @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1, @RequestParam(name = "id", defaultValue = "2") Long id,
            @RequestParam(name = "action") String action, Model model,
            ModelMap modelMap) {

        if (action.equals("draft")) {
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
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("lessonDTO", new LessonVideoDTO());


        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep2Content :: step2Content");

        return "instructorDashboard/index";
//        return "instructorDashboard/CreateCourseStep2";
    }

    //create chapter
    @PostMapping("/viewcourse/chaptersadd")
    public String addNewChapter(@ModelAttribute("chapter") ChapterDTO chapterDTO,
                                @RequestParam(name = "courseId") Long courseId,
                                Model model,
                                RedirectAttributes redirectAttributes
    ) {
        chapterDTO.setCourseId(courseId);
        List<Chapter> existingChapters = chapterService.chapterListByCourse(courseId);
        //tim dc list chapter by courseId
        int nextOrderNumber = existingChapters.size() + 1;
        chapterDTO.setOrderNumberChapter(nextOrderNumber);
        chapterService.saveChapter(chapterDTO);
        return "redirect:/instructordashboard/viewcourse/addcoursestep2?courseId=" + courseId;

    }

    //create lesson
    @PostMapping("/viewcourse/lessonadd")
    public String addNewLesson(@ModelAttribute("lessonDTO") LessonVideoDTO lessonVideoDTO, RedirectAttributes redirectAttributes) {
        List<Lesson> existingLessons = lessonService.findLessonsByChapterId(lessonVideoDTO.getChapterId());
        //tim dc list lesson by chapterId
        int nextOrderNumber = existingLessons.size() + 1;
        lessonVideoDTO.setOrderNumber(nextOrderNumber);
//        lessonService.createLesson(lessonVideoDTO);
        Long chapterId = lessonVideoDTO.getChapterId();
        Long courseId = chapterService.getChapterById(chapterId).getCourse().getCourseId();
        return "redirect:/instructordashboard/viewcourse/addcoursestep2?courseId=" + courseId;
    }

    //up screen after add chapter, lesson step 2
    @GetMapping("/viewcourse/addcoursestep2")
    public String showStep2Page(@RequestParam(name = "courseId") Long courseId, Model model) {
        ChapterDTO chapterDTO = new ChapterDTO();
        chapterDTO.setCourseId(courseId);
        model.addAttribute("chapter", chapterDTO);

        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
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
        model.addAttribute("lessonDTO", new LessonVideoDTO());


        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep2Content :: step2Content");

        return "instructorDashboard/index";
//        return "instructorDashboard/CreateCourseStep2";
    }

    //save step 2 and next step 3
    @PostMapping("/viewcourse/addcoursestep3")
    public String step3(@RequestParam(name = "courseId", required = false) Long courseId,
                        Model model, @RequestParam(name = "action") String action,
                        @RequestParam(name = "id", defaultValue = "2") Long userid, ModelMap modelMap) {
        if (action.equals("draft")) { //khi nay la draft khi o step 2
//            Course course = courseService.createCourseStep2(courseId);
            List<CourseDTO> courseList = courseService.findCourseByUserId(userid);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        if (action.equals("previousstep3")) {
            Course course = courseService.findCourseById(courseId);
            model.addAttribute("course", course);
            model.addAttribute("courseId", courseId);
            if (course.getPrice() == null) {
                model.addAttribute("coursestep3", new AddCourseStep3DTO());
            } else {
                AddCourseStep3DTO dto = new AddCourseStep3DTO();
                dto.setPrice(course.getPrice());
                model.addAttribute("coursestep3", dto);
            }

            model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep3Content :: step3Content");

            return "instructorDashboard/index";
//            return "instructorDashboard/CreateCourseStep3";
        }
//        Course course = courseService.createCourseStep2(courseId);
        Course course = courseService.findCourseById(courseId); //tam
        model.addAttribute("course", course);
        model.addAttribute("courseId", courseId);
        if (course.getPrice() == null) {
            model.addAttribute("coursestep3", new AddCourseStep3DTO());
        } else {
            AddCourseStep3DTO dto = new AddCourseStep3DTO();
            dto.setPrice(course.getPrice());
            model.addAttribute("coursestep3", dto);
        }

        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep3Content :: step3Content");

        return "instructorDashboard/index";
//        return "instructorDashboard/CreateCourseStep3";
    }

    //save all and submit
    @PostMapping("/viewcourse/addcoursestep4")
    public String step4(@ModelAttribute("coursestep3") AddCourseStep3DTO courseStep3,
                        @RequestParam(name = "courseId", required = false) Long courseId,
                        Model model, @RequestParam(name = "action") String action,
                        @RequestParam(name = "id", defaultValue = "2") Long userid, ModelMap modelMap) {
        if (action.equals("draft")) {
            Course course = courseService.createCourseStep3(courseId, courseStep3);
            List<CourseDTO> courseList = courseService.findCourseByUserId(userid);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        if (action.equals("previousstep")) {
            return "redirect:/instructordashboard/viewcourse/addcoursestep2?courseId=" + courseId;
        }

        Course course = courseService.createCourseStep3(courseId, courseStep3);
        model.addAttribute("courseId", courseId);

        model.addAttribute("fragmentContent", "instructorDashboard/fragments/CreateCourseStep4Content :: step4Content");

        return "instructorDashboard/index";
//        return "instructorDashboard/CreateCourseStep4";
    }

    //set status=pending after create = submit with condition
    @PostMapping("/viewcourse/submitcourse")
    public String submitCourse(@RequestParam(name = "courseId", required = false) Long courseId,
                               Model model, @RequestParam(name = "action") String action,
                               @RequestParam(name = "id", defaultValue = "2") Long userid, ModelMap modelMap) {
        if (action.equals("draft")) {
            Course course = courseService.submitCourse(courseId, "draft");
            List<CourseDTO> courseList = courseService.findCourseByUserId(userid);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        Course course = courseService.submitCourse(courseId, "pending");
        model.addAttribute("course", course);
        model.addAttribute("courseId", courseId);
        return "redirect:/instructordashboard/viewcourse"; //in ra cai detail course thi hay hon
    }

    //deleteCourse
    @PostMapping("/viewcourse/deletecourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/instructordashboard/viewcourse";
    }
}
