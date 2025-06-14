package com.OLearning.controller;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseContentDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.service.LessonChapterService;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.quiz.QuizService;
import com.OLearning.service.video.VideoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    @Autowired
    private LessonChapterService lessonChapterService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private QuizService quizService;
    @Autowired
    private LessonRepository lessonRepository;

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
    //view course media
    @PostMapping("/createcourse/coursemedia")
    public String saveCourseBasic(@Valid @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1,
                                  BindingResult result,
                                  @RequestParam(name = "id", defaultValue = "2") Long id,
                                  @RequestParam(name = "action") String action,
                                  Model model,
                                  ModelMap modelMap,
                                  HttpSession session) {
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
        session.setAttribute("course", course);
        courseStep1.setId(course.getCourseId());//gan lai id cua course vao DTO
        Long courseId = course.getCourseId();
        model.addAttribute("courseId", courseId);
        model.addAttribute("coursestep2", new CourseMediaDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step2CourseMedia :: step2Content");
        return "instructorDashboard/indexUpdate";
    }

    //save course media and next to course content
    //view course content
    @PostMapping("/createcourse/coursecontent")
    public String saveCourseMedia(@Valid @ModelAttribute("coursestep2") CourseMediaDTO courseMediaDTO,
                                  BindingResult result,
                                  @RequestParam(name = "id", defaultValue = "2") Long id,
                                  Model model,
                                  @RequestParam(name = "action") String action,
                                  HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("coursestep2", courseMediaDTO);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step2CourseMedia :: step2Content");
            return "instructorDashboard/indexUpdate";
        }
        if (action.equalsIgnoreCase("draft")) {
            Course course = courseService.createCourseMedia(courseMediaDTO.getCourseId(), courseMediaDTO);
            return "redirect:../courses";
        }
        if (action.equalsIgnoreCase("previousstep")) {
            Course course = session.getAttribute("course") != null ? (Course) session.getAttribute("course") : new Course();
            AddCourseStep1DTO courseStep1 = courseService.draftCourseStep1(course);
            model.addAttribute("coursestep1", courseStep1);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }

        Course course = courseService.createCourseMedia(courseMediaDTO.getCourseId(), courseMediaDTO);
        session.setAttribute("course", course);// gan lai course vao session de dung cho cac buoc tiep theo
        //tao demo chapter
        ChapterDTO chapterDTO = new ChapterDTO();
        model.addAttribute("chapter", chapterDTO);
        List<Chapter> chapters = chapterService.chapterListByCourse(course.getCourseId());
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("lessontitle", new LessonTitleDTO());
        model.addAttribute("videoDTO", new VideoDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
        return "instructorDashboard/indexUpdate";
    }

    //save chapter in course Content
    @PostMapping("/createcourse/chaptersadd")
    public String addNewChapter(@Valid @ModelAttribute("chapter") ChapterDTO chapterDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session
    ) {
        Long courseId = ((Course) session.getAttribute("course")).getCourseId();
        if (result.hasErrors()) {
            model.addAttribute("showForm", true);
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            model.addAttribute("chapter", chapterDTO);
            model.addAttribute("chapters", chapters);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
            return "instructorDashboard/indexUpdate";
        }
        chapterDTO.setCourseId(courseId);
        List<Chapter> existingChapters = chapterService.chapterListByCourse(courseId);
        if (chapterDTO.getOrderNumberChapter() == null || chapterDTO.getOrderNumberChapter() == 0) {
            int maxOrderNumber = existingChapters.stream()
                    .mapToInt(Chapter::getOrderNumber)
                    .max()
                    .orElse(0);
            chapterDTO.setOrderNumberChapter(maxOrderNumber + 1);
        } else {
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            for (Chapter chapter : chapters) {
                if (chapter.getOrderNumber() == chapterDTO.getOrderNumberChapter()) {
                    model.addAttribute("errorMessage", "Order number already exists. Please choose a different order number.");
                    model.addAttribute("chapter", chapterDTO);
                    model.addAttribute("lessontitle", new LessonTitleDTO());
                    model.addAttribute("chapters", chapters);
                    model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
                    return "instructorDashboard/indexUpdate";
                }
            }
        }
        chapterService.saveChapter(chapterDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Chapter added successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    //delete chapter
    @PostMapping("/createcourse/deletechapter")
    public String deleteChapter(@RequestParam(name = "chapterId") Long chapterId
            , RedirectAttributes redirectAttributes,
                                HttpSession session) {
        lessonChapterService.deleteChapter(chapterId);
        redirectAttributes.addFlashAttribute("errorMessage", "Chapter deleted successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    //save lesson title
    @PostMapping("/createcourse/addlesson")
    public String addNewLesson(@Valid @ModelAttribute("lessontitle") LessonTitleDTO lessonTitleDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpSession session,
                               @RequestParam(name = "chapterId") Long chapterId) {
        Long courseId = ((Course) session.getAttribute("course")).getCourseId();
        if (result.hasErrors()) {
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            model.addAttribute("chapters", chapters);
            //khong biet co phai tim chapterDTO de hien thi lai khong nhi
            model.addAttribute("chapter", new ChapterDTO());
            model.addAttribute("lessontitle", lessonTitleDTO);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
            return "instructorDashboard/indexUpdate";
        }
        lessonTitleDTO.setChapterId(chapterId);
        List<Lesson> existingLessons = lessonService.findLessonsByChapterId(chapterId);
        if (lessonTitleDTO.getOrderNumber() == null || lessonTitleDTO.getOrderNumber() == 0) {
            int maxOrderNumber = existingLessons.stream()
                    .mapToInt(Lesson::getOrderNumber)
                    .max()
                    .orElse(0);
            lessonTitleDTO.setOrderNumber(maxOrderNumber + 1);
        } else {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapterId);
            for (Lesson lesson : lessons) {
                if (lesson.getOrderNumber() == lessonTitleDTO.getOrderNumber()) {
                    model.addAttribute("errorMessage", "Order number already exists. Please choose a different order number.");
                    model.addAttribute("chapter", new ChapterDTO());
                    model.addAttribute("lessontitle", lessonTitleDTO);
                    model.addAttribute("videoDTO", new VideoDTO());
                    List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
                    model.addAttribute("chapters", chapters);
                    model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
                    return "instructorDashboard/indexUpdate";
                }
            }
        }
        lessonChapterService.createLesson(lessonTitleDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Lesson added successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    //create video
    @PostMapping("/createcourse/addvideo")
    public String addVideo(@ModelAttribute("videoDTO") VideoDTO videoDTO,
                           @RequestParam(name = "lessonId") Long lessonId,
                           RedirectAttributes redirectAttributes) {
        try {
            // Save video and associate with lesson
            Video video = videoService.saveVideo(videoDTO, lessonId);
            lessonService.updateContentType(lessonId, "video");
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
            lesson.setDuration(videoDTO.getDuration());
            lesson.setUpdatedAt(LocalDateTime.now());
            lesson.setVideo(video);
            lessonRepository.save(lesson);

            redirectAttributes.addFlashAttribute("successMessage", "Video added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding video: " + e.getMessage());
        }
        return "redirect:../createcourse/coursecontent";
    }

    //delete lesson
    @PostMapping("/createcourse/deletelesson")
    public String deleteLesson(@RequestParam(name = "lessonId") Long lessonId
            , RedirectAttributes redirectAttributes) {
        lessonService.deleteAllFkByLessonId(lessonId);
        redirectAttributes.addFlashAttribute("errorMessage", "Lesson deleted successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    //view course sau khi create chapter or lesson
    @GetMapping("createcourse/coursecontent")
    public String showCourseContent(Model model, HttpSession session) {
        ChapterDTO chapterDTO = new ChapterDTO();
        model.addAttribute("chapter", chapterDTO);
        Long courseId = ((Course) session.getAttribute("course")).getCourseId();
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
        model.addAttribute("lessontitle", new LessonTitleDTO());
        model.addAttribute("videoDTO", new VideoDTO());
        model.addAttribute("quizDTO", new QuizDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/Step3CourseContent :: step3Content");
        return "instructorDashboard/indexUpdate";
    }

    //create quiz
    @PostMapping("/createcourse/addquiz")
    public String addQuiz(@RequestParam("lessonId") Long lessonId,
                          @RequestParam("question") List<String> questions,
                          @RequestParam("optionA") List<String> optionsA,
                          @RequestParam("optionB") List<String> optionsB,
                          @RequestParam("optionC") List<String> optionsC,
                          @RequestParam("optionD") List<String> optionsD,
                          @RequestParam("correctAnswer") List<String> correctAnswers,
                          RedirectAttributes redirectAttributes) {
        try {
            // Update lesson content type
            lessonService.updateContentType(lessonId, "quiz");
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
            lesson.setUpdatedAt(LocalDateTime.now());

            // Save each quiz question
            for (int i = 0; i < questions.size(); i++) {
                QuizDTO quizDTO = new QuizDTO();
                quizDTO.setQuestion(questions.get(i));
                quizDTO.setOptionA(optionsA.get(i));
                quizDTO.setOptionB(optionsB.get(i));
                quizDTO.setOptionC(optionsC.get(i));
                quizDTO.setOptionD(optionsD.get(i));
                quizDTO.setCorrectAnswer(correctAnswers.get(i));
                quizDTO.setOrderNumber(i + 1);

                // Save quiz and associate with lesson
                Quiz quiz = quizService.saveQuiz(quizDTO, lessonId);
                lesson.getQuizzes().add(quiz);
            }
            lessonRepository.save(lesson);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding quiz: " + e.getMessage());
        }
        return "redirect:../createcourse/coursecontent";
    }

    //get quizzes for a lesson
    @GetMapping("/createcourse/getquizzes")
    @ResponseBody
    public List<Quiz> getQuizzes(@RequestParam("lessonId") Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        return lesson.getQuizzes();
    }
}
