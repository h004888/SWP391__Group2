package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.dto.quiz.QuizDTO;
import com.OLearning.dto.quiz.QuizQuestionDTO;
import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.*;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.QuizRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.quiz.QuizService;
import com.OLearning.service.courseChapterLesson.CourseChapterService;
import com.OLearning.service.courseChapterLesson.LessonChapterService;
import com.OLearning.service.courseChapterLesson.LessonQuizService;
import com.OLearning.service.user.UserService;
import com.OLearning.service.video.VideoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/instructordashboard")
public class ControlllerAddCourse {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonQuizService lessonQuizService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonChapterService lessonChapterService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private QuizService quizService;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseChapterService courseChapterService;
    @Autowired
    private UserService userService;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private CourseRepository courseRepository;

    //dashhboard
    @GetMapping()
    public String dashboard(Model model) {
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/content :: contentMain");

        return "instructorDashboard/indexUpdate";
    }

    //viewAllCourses
    @GetMapping("/courses")
    public String viewCourse(@RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "7") int size,
                             Model model, ModelMap modelMap) {
        //lay ra course tu userId dang nhap
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Page<CourseDTO> coursePage = courseService.findCourseByUserId(userId, page, size);
        modelMap.put("courses", coursePage.getContent());
        modelMap.put("currentPage", page);
        modelMap.put("totalPages", coursePage.getTotalPages());
        modelMap.put("totalElements", coursePage.getTotalElements());
        modelMap.put("size", size);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/coursesContent :: listsCourseContent");
        return "instructorDashboard/indexUpdate";
    }

    //search course
    @GetMapping("courses/searchcourse")
    public String searchCourse(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            Model model, ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        Page<CourseDTO> coursePage = courseService.searchCourse(userId, title, page, size);
        modelMap.put("courses", coursePage.getContent());
        modelMap.put("currentPage", page);
        modelMap.put("totalPages", coursePage.getTotalPages());
        modelMap.put("totalElements", coursePage.getTotalElements());
        modelMap.put("size", size);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/coursesContent :: listsCourseContent");
        return "instructorDashboard/indexUpdate";
    }

    //filter Course
    @GetMapping("courses/filter")
    public String filterCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            Model model) {

        // Lấy ID của người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Long categoryId = null;
        if (category != null && !category.isEmpty()) {
            try {
                categoryId = Long.parseLong(category);
            } catch (NumberFormatException e) {
            }
        }

        Page<CourseDTO> coursePage = courseService.filterCoursesInstructorManage(
                userId, categoryId, status, price, page, size);

        List<Category> categories = categoryService.findAll();

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());
        model.addAttribute("totalElements", coursePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("selectedPrice", price);

        return "instructorDashboard/fragments/coursesContent :: listsCourseContent";
    }
    //delete course
    @PostMapping("/createcourse/deletecourse")
    public String deletecourse(@RequestParam(name = "courseId") Long courseId
            , RedirectAttributes redirectAttributes, HttpServletRequest request) {
        courseChapterService.deleteCourseFK(courseId);
        redirectAttributes.addFlashAttribute("errorMessage", "course deleted successfully.");
        return "redirect:../courses";
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
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("courseId", courseId);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/courseDetailContent_new :: courseDetailContent");
        return "instructorDashboard/indexUpdate";
    }

    //create Course Basic ìnformation
    //view course basic
    @GetMapping("/createcourse/coursebasic")
    public String addNewCourse(@RequestParam(name = "courseId", required = false) Long courseId, Model model) {
        if (courseId == null) {
            model.addAttribute("coursestep1", new AddCourseStep1DTO());//tao doi tương AddCourseStep1DTO de luu thong tin
            model.addAttribute("categories", categoryService.findAll());//in ra thong tin category
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }
        //khi nay la khi draft xong roi ma minh muon update lai course
        Course course = courseService.findCourseById(courseId); // tim course theo courseId
        AddCourseStep1DTO coursestep1 = courseService.draftCourseStep1(course);// chuyen doi course sang AddCourseStep1DT0
        // ---chuyen doi course da draft sang AddCourseStep1DTO
        model.addAttribute("coursestep1", coursestep1);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/step1BasicInfor :: step1Content");
        return "instructorDashboard/indexUpdate";
    }

    //ham lay cookieCourseId tu cookie
    private Long getCourseIdFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("cookieCourseId".equals(cookie.getName())) {
                    try {
                        return Long.parseLong(cookie.getValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    //next to save course basic information
    //view course media
    @PostMapping("/createcourse/coursemedia")
    public String saveCourseBasic(@Valid @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1,
                                  BindingResult result,
                                  @RequestParam(name = "action") String action,
                                  Model model,
                                  HttpServletResponse response) {
        if (result.hasErrors()) {
            result.getFieldErrors().forEach(fieldError -> {
                if (fieldError.getCode().equals("typeMismatch") && fieldError.getField().equals("price")) {
                    model.addAttribute("priceTypeError", "Price must be a number");
                }
            });
            model.addAttribute("coursestep1", courseStep1);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }
        if (action.equalsIgnoreCase("draft")) {
            Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);
            courseStep1.setId(course.getCourseId());
            return "redirect:../courses";
        }

        Course course = courseService.createCourseStep1(courseStep1.getId(), courseStep1);//course=null thi se tao moi, con khong thi se update lai
        Cookie courseCookie = new Cookie("cookieCourseId", course.getCourseId().toString());
        courseCookie.setMaxAge(60 * 60 * 24);
        courseCookie.setPath("/");
        response.addCookie(courseCookie);
        courseStep1.setId(course.getCourseId());//gan lai id cua course vao DTO
        Long courseId = course.getCourseId();
        model.addAttribute("courseId", courseId);
        model.addAttribute("coursestep2", new CourseMediaDTO());
        model.addAttribute("imageUrl", course.getCourseImg());
        model.addAttribute("videoUrl", course.getVideoUrlPreview()); 
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/step2CourseMedia :: step2Content");
        return "instructorDashboard/indexUpdate";
    }

    //save course media and next to course content
    //view course content
    @PostMapping("/createcourse/coursecontent")
    public String saveCourseMedia(@Valid @ModelAttribute("coursestep2") CourseMediaDTO courseMediaDTO,
                                  BindingResult result,
                                  Model model,
                                  @RequestParam(name = "action") String action,
                                  HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("coursestep2", courseMediaDTO);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step2CourseMedia :: step2Content");
            return "instructorDashboard/indexUpdate";
        }

        // Lấy courseId từ DTO hoặc cookie nếu không có
        Long courseId = courseMediaDTO.getCourseId();
        if (courseId == null) {
            courseId = getCourseIdFromCookie(request);
            courseMediaDTO.setCourseId(courseId);
        }

        if (action.equalsIgnoreCase("draft")) {
            Course course = courseService.createCourseMedia(courseId, courseMediaDTO);
            courseService.submitCourse(course.getCourseId(), "draft");
            return "redirect:../courses";
        }
        if (action.equalsIgnoreCase("previousstep")) {
            Course course = courseService.findCourseById(courseId);
            AddCourseStep1DTO courseStep1 = courseService.draftCourseStep1(course);
            model.addAttribute("coursestep1", courseStep1);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step1BasicInfor :: step1Content");
            return "instructorDashboard/indexUpdate";
        }

        Course course = courseService.createCourseMedia(courseId, courseMediaDTO);
        ChapterDTO chapterDTO = new ChapterDTO();
        model.addAttribute("chapter", chapterDTO);
        List<Chapter> chapters = chapterService.chapterListByCourse(course.getCourseId());
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
            if (lessons != null && lessons.size() > 0) {
                chapter.setLessons(lessons);
            }
        }
        if (chapters != null && !chapters.isEmpty()) {
            model.addAttribute("chapters", chapters);
        }
        model.addAttribute("lessontitle", new LessonTitleDTO());
        model.addAttribute("videoDTO", new VideoDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
        return "instructorDashboard/indexUpdate";
    }

    //save chapter in course Content
    @PostMapping("/createcourse/chaptersadd")
    public String addNewChapter(@Valid @ModelAttribute("chapter") ChapterDTO chapterDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Long courseId = getCourseIdFromCookie(request);
        if (result.hasErrors()) {
            model.addAttribute("showForm", true);
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            model.addAttribute("chapter", chapterDTO);
            model.addAttribute("chapters", chapters);
            model.addAttribute("lessontitle", new LessonTitleDTO());
            model.addAttribute("videoDTO", new VideoDTO());
            model.addAttribute("quizDTO", new QuizDTO());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
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
                    model.addAttribute("videoDTO", new VideoDTO());
                    model.addAttribute("quizDTO", new QuizDTO());
                    model.addAttribute("lessontitle", new LessonTitleDTO());
                    model.addAttribute("chapters", chapters);
                    model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
                    return "instructorDashboard/indexUpdate";
                }
            }
        }
        chapterService.saveChapter(chapterDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Chapter added successfully.");
        return "redirect:../createcourse/coursecontent";
    }
    //update chapter
    @PostMapping("/createcourse/updatechapter")
    public String updateChapter(@RequestParam("chapterId") Long chapterId,
                                @RequestParam("title") String title,
                                @RequestParam("description") String description,
                                @RequestParam("orderNumber") Integer orderNumber,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        Long courseId = getCourseIdFromCookie(request);
        // Kiểm tra orderNumber có bị trùng không (trừ chính chapter đang update)
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            if (!chapter.getChapterId().equals(chapterId) && chapter.getOrderNumber().equals(orderNumber)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order number already exists. Please choose a different order number.");
                return "redirect:../createcourse/coursecontent";
            }
        }
        Chapter chapter = chapterService.getChapterById(chapterId);// tim dc chapter theo chapter Id
        if (chapter == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chapter not found.");
            return "redirect:../createcourse/coursecontent";
        }
        //update lai thong tin trong chapter do
        chapter.setTitle(title);
        chapter.setDescription(description);
        chapter.setOrderNumber(orderNumber);
        chapter.setUpdatedAt(java.time.LocalDateTime.now());
        chapterService.updateChapter(chapter);
        // Lưu lại ID để đảm bảo update đúng chapter
        chapter.setChapterId(chapterId);
        redirectAttributes.addFlashAttribute("successMessage", "Chapter updated successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    //delete chapter
    @PostMapping("/createcourse/deletechapter")
    public String deleteChapter(@RequestParam(name = "chapterId") Long chapterId
            , RedirectAttributes redirectAttributes, HttpServletRequest request) {
        lessonChapterService.deleteChapter(chapterId);
        redirectAttributes.addFlashAttribute("errorMessage", "Chapter deleted successfully.");
        int totalLesson = 0;
        int totalDuration = 0;
        Long courseId = getCourseIdFromCookie(request);
        Course course = courseService.findCourseById(courseId);
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
            if (lessons != null && lessons.size() > 0) {
                totalLesson += lessons.size();
                for (Lesson lesson : lessons) {
                    if (lesson.getDuration() != null) {
                        totalDuration += lesson.getDuration();
                    }
                }
            }
        }
        courseService.saveCourse(courseId);
        return "redirect:../createcourse/coursecontent";
    }

    //save lesson title
    @PostMapping("/createcourse/addlesson")
    public String addNewLesson(@Valid @ModelAttribute("lessontitle") LessonTitleDTO lessonTitleDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               @RequestParam(name = "chapterId") Long chapterId) {
        Long courseId = getCourseIdFromCookie(request);
        if (result.hasErrors()) {
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            model.addAttribute("chapters", chapters);
            model.addAttribute("chapter", new ChapterDTO());
            model.addAttribute("lessontitle", lessonTitleDTO);
            model.addAttribute("videoDTO", new VideoDTO());
            model.addAttribute("quizDTO", new QuizDTO());
            model.addAttribute("quizQuestionDTO", new QuizQuestionDTO());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
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
                    model.addAttribute("quizDTO", new QuizDTO());
                    model.addAttribute("quizQuestionDTO", new QuizQuestionDTO());
                    List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
                    model.addAttribute("chapters", chapters);
                    model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
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
            System.out.println("Adding video for lesson ID: " + lessonId);
            System.out.println("Video duration received: " + videoDTO.getDuration());

            // Đảm bảo duration không null
            if (videoDTO.getDuration() == null) {
                videoDTO.setDuration(0);
                System.out.println("Duration was null, set to default 0");
            }

            // Save video and associate with lesson
            Video video = videoService.saveVideo(videoDTO, lessonId);

            // Update lesson
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
            lesson.setContentType("video");
            
            lesson.setUpdatedAt(LocalDateTime.now());
            lesson.setVideo(video);
            lesson.setDuration(videoDTO.getDuration());
            lessonRepository.save(lesson);

            // Update course totals
            updateCourseTotals(lesson.getChapter().getCourse().getCourseId());

            redirectAttributes.addFlashAttribute("successMessage", "Video added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding video: " + e.getMessage());
        }
        return "redirect:../createcourse/coursecontent";
    }

    //delete lesson
    @PostMapping("/createcourse/deletelesson")
    public String deleteLesson(@RequestParam(name = "lessonId") Long lessonId
            , RedirectAttributes redirectAttributes, HttpServletRequest request) {
        lessonQuizService.deleteAllFkByLessonId(lessonId);
        redirectAttributes.addFlashAttribute("errorMessage", "Lesson deleted successfully.");
        int totalLesson = 0;
        int totalDuration = 0;
        Long courseId = getCourseIdFromCookie(request);
        Course course = courseService.findCourseById(courseId);
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
            if (lessons != null && lessons.size() > 0) {
                totalLesson += lessons.size();
                for (Lesson lesson : lessons) {
                    if (lesson.getDuration() != null) {
                        totalDuration += lesson.getDuration();
                    }
                }
            }
        }

        courseService.saveCourse(courseId);
        return "redirect:../createcourse/coursecontent";
    }

    //view course sau khi create chapter or lesson
    @GetMapping("createcourse/coursecontent")
    public String showCourseContent(Model model, HttpServletRequest request) {
        ChapterDTO chapterDTO = new ChapterDTO();
        model.addAttribute("chapter", chapterDTO);
        Long courseId = getCourseIdFromCookie(request);
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
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
        model.addAttribute("quizQuestionDTO", new QuizQuestionDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/step3CourseContent :: step3Content");
        return "instructorDashboard/indexUpdate";
    }

    //create quiz
    @PostMapping("/createcourse/addquiz")
    public String addQuiz(@RequestParam("lessonId") Long lessonId,
                          @RequestParam("title") String title,
                          @RequestParam("description") String description,
                          @RequestParam("timeLimit") Integer timeLimit,
                          @RequestParam(value = "question", required = false) List<String> questions,
                          @RequestParam(value = "optionA", required = false) List<String> optionsA,
                          @RequestParam(value = "optionB", required = false) List<String> optionsB,
                          @RequestParam(value = "optionC", required = false) List<String> optionsC,
                          @RequestParam(value = "optionD", required = false) List<String> optionsD,
                          @RequestParam(value = "correctAnswer", required = false) List<String> correctAnswers,
                          RedirectAttributes redirectAttributes) {
        try {
            // Check if parameters are valid
            if (questions == null || questions.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No quiz questions provided");
                return "redirect:../createcourse/coursecontent";
            }

            // Create quiz DTO
            QuizDTO quizDTO = new QuizDTO();
            quizDTO.setTitle(title);
            quizDTO.setDescription(description);
            quizDTO.setTimeLimit(timeLimit);
            quizDTO.setLessonId(lessonId);

            // Add questions to quiz
            List<QuizQuestionDTO> questionDTOs = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                QuizQuestionDTO questionDTO = new QuizQuestionDTO();
                questionDTO.setQuestion(questions.get(i));
                questionDTO.setOptionA(optionsA.get(i));
                questionDTO.setOptionB(optionsB.get(i));
                questionDTO.setOptionC(optionsC.get(i));
                questionDTO.setOptionD(optionsD.get(i));
                questionDTO.setCorrectAnswer(correctAnswers.get(i));
                questionDTO.setOrderNumber(i + 1);
                questionDTOs.add(questionDTO);
            }
            quizDTO.setQuestions(questionDTOs);

            // Save quiz
            Quiz quiz = quizService.saveQuiz(quizDTO, lessonId);

            // Update lesson content type
            Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
            lesson.setContentType("quiz");
            lesson.setUpdatedAt(LocalDateTime.now());
            lesson.setDuration(quiz.getTimeLimit());
            lessonRepository.save(lesson);

            redirectAttributes.addFlashAttribute("successMessage", "Quiz added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding quiz: " + e.getMessage());
        }
        return "redirect:../createcourse/coursecontent";
    }

    //save totalduration va total lesson in course content and next to submit
    @PostMapping("/createcourse/submitcourse")
    public String saveCourseContent(RedirectAttributes redirectAttributes,
                                    @RequestParam(name = "action") String action,
                                    Model model,
                                    HttpServletRequest request) {
        Long courseId = getCourseIdFromCookie(request);
        if (courseId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course ID not found. Please start the course creation process again.");
            return "redirect:../createcourse/coursecontent";
        }
        Course course = courseService.findCourseById(courseId);
        if (action.equalsIgnoreCase("previousstep")) {
            CourseMediaDTO courseMediaDTO = new CourseMediaDTO();
            courseMediaDTO.setCourseId(course.getCourseId());
            model.addAttribute("courseId", course.getCourseId());
            model.addAttribute("coursestep2", courseMediaDTO);
            model.addAttribute("imageUrl", course.getCourseImg());
            model.addAttribute("videoUrl", course.getVideoUrlPreview()); 
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/step2CourseMedia :: step2Content");
            return "instructorDashboard/indexUpdate";
        }
        int totalLesson = 0;
        int totalDuration = 0;
        List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
            if (lessons != null && lessons.size() > 0) {
                totalLesson += lessons.size();
                for (Lesson lesson : lessons) {
                    if (lesson.getDuration() != null) {
                        totalDuration += lesson.getDuration();
                    }
                }
            }
        }

        courseService.saveCourse(courseId);
        if (action.equalsIgnoreCase("draft")) {
            courseService.submitCourse(courseId, "draft");
            return "redirect:../courses";
        }
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/step4SubmitCourse :: step4Content");
        return "instructorDashboard/indexUpdate";
    }

    //submit course
    @PostMapping("/createcourse/savecourse")
    public String submitCourse(Model model, @RequestParam(name = "action") String action,
                               HttpServletRequest request, HttpServletResponse response) {

        Long courseId = getCourseIdFromCookie(request);
        if (action.equalsIgnoreCase("draft")) {
            courseService.submitCourse(courseId, "draft");
            return "redirect:../courses";
        }
        if (action.equalsIgnoreCase("previousstep")) {
            return "redirect:../createcourse/coursecontent";
        }
        courseService.submitCourse(courseId, "pending");
        Cookie cookie = new Cookie("cookieCourseId", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/courseAdded :: courseAdded");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/createcourse/updatelesson")
    public String updateLesson(@RequestParam("lessonId") Long lessonId,
                               @RequestParam("title") String title,
                               @RequestParam("description") String description,
                               @RequestParam("orderNumber") Integer orderNumber,
                               @RequestParam(value = "isFree", required = false) Boolean isFree,
                               @RequestParam("contentType") String contentType,
                               @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                               @RequestParam(value = "duration", required = false) Integer duration,
                               @RequestParam(value = "quizTitle", required = false) String quizTitle,
                               @RequestParam(value = "quizDescription", required = false) String quizDescription,
                               @RequestParam(value = "quizTimeLimit", required = false) Integer quizTimeLimit,
                               @RequestParam(value = "questions[]", required = false) List<String> questions,
                               @RequestParam(value = "optionAs[]", required = false) List<String> optionAs,
                               @RequestParam(value = "optionBs[]", required = false) List<String> optionBs,
                               @RequestParam(value = "optionCs[]", required = false) List<String> optionCs,
                               @RequestParam(value = "optionDs[]", required = false) List<String> optionDs,
                               @RequestParam(value = "correctAnswers[]", required = false) List<String> correctAnswers,
                               @RequestParam(value = "questionIds[]", required = false) List<Long> questionIds,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            // Lấy lesson hiện tại
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));
            
            // Lấy chapterId từ lesson
            Long chapterId = lesson.getChapter().getChapterId();
            
            // Kiểm tra trùng order number (trừ chính lesson đang update)
            if (orderNumber != null) {
                List<Lesson> lessons = lessonService.findLessonsByChapterId(chapterId);
                for (Lesson existingLesson : lessons) {
                    if (!existingLesson.getLessonId().equals(lessonId) && 
                        existingLesson.getOrderNumber().equals(orderNumber)) {
                        redirectAttributes.addFlashAttribute("errorMessage", 
                            "Order number already exists. Please choose a different order number.");
                        return "redirect:../createcourse/coursecontent";
                    }
                }
            }
            
            // Cập nhật thông tin cơ bản
            lesson.setTitle(title);
            lesson.setDescription(description);
            lesson.setOrderNumber(orderNumber);
            lesson.setIsFree(isFree != null ? isFree : false);
            lesson.setUpdatedAt(LocalDateTime.now());
            lesson.setContentType(contentType);
            
            // Xử lý theo loại nội dung
            if ("video".equals(contentType)) {
                // Cập nhật video
                if (videoFile != null && !videoFile.isEmpty()) {
                    VideoDTO videoDTO = new VideoDTO();
                    videoDTO.setVideoUrl(videoFile);
                    
                    // Cập nhật duration nếu có
                    if (duration != null) {
                        lesson.setDuration(duration);
                    }
                    
                    Video video = videoService.saveVideo(videoDTO, lessonId);
                    lesson.setVideo(video);
                }
                
                // Xóa quiz nếu có
                if (lesson.getQuiz() != null) {
                    Quiz quiz = lesson.getQuiz();
                    lesson.setQuiz(null);
                    lessonRepository.save(lesson);
                    quizService.deleteQuiz(quiz.getId());
                }
            } else if ("quiz".equals(contentType)) {
                // Cập nhật quiz
                QuizDTO quizDTO = new QuizDTO();
                quizDTO.setTitle(quizTitle);
                quizDTO.setDescription(quizDescription);
                quizDTO.setTimeLimit(quizTimeLimit);
                quizDTO.setLessonId(lessonId);
                
                // Nếu đã có quiz, cập nhật quiz đó
                if (lesson.getQuiz() != null) {
                    quizDTO.setId(lesson.getQuiz().getId());
                }
                
                // Thêm câu hỏi vào quiz nếu có
                if (questions != null && !questions.isEmpty()) {
                    List<QuizQuestionDTO> questionDTOs = new ArrayList<>();
                    
                    for (int i = 0; i < questions.size(); i++) {
                        QuizQuestionDTO questionDTO = new QuizQuestionDTO();
                        
                        // Nếu có ID câu hỏi, đây là câu hỏi đã tồn tại
                        if (questionIds != null && i < questionIds.size() && questionIds.get(i) != null) {
                            questionDTO.setId(questionIds.get(i));
                        }
                        
                        questionDTO.setQuestion(questions.get(i));
                        questionDTO.setOptionA(optionAs != null && i < optionAs.size() ? optionAs.get(i) : "");
                        questionDTO.setOptionB(optionBs != null && i < optionBs.size() ? optionBs.get(i) : "");
                        questionDTO.setOptionC(optionCs != null && i < optionCs.size() ? optionCs.get(i) : "");
                        questionDTO.setOptionD(optionDs != null && i < optionDs.size() ? optionDs.get(i) : "");
                        questionDTO.setCorrectAnswer(correctAnswers != null && i < correctAnswers.size() ? correctAnswers.get(i) : "A");
                        questionDTO.setOrderNumber(i + 1);
                        
                        questionDTOs.add(questionDTO);
                    }
                    
                    quizDTO.setQuestions(questionDTOs);
                }
                
                Quiz quiz = quizService.saveQuiz(quizDTO, lessonId);
                lesson.setQuiz(quiz);
                lesson.setDuration(quizTimeLimit);
                
                // Xóa video nếu có
                if (lesson.getVideo() != null) {
                    Video video = lesson.getVideo();
                    lesson.setVideo(null);
                    lessonRepository.save(lesson);
                    videoRepository.delete(video);
                }
            } else {
                // Basic lesson (chỉ có title và description)
                lesson.setContentType("basic");
                lesson.setDuration(0);
                
                // Xóa cả video và quiz nếu có
                if (lesson.getVideo() != null) {
                    Video video = lesson.getVideo();
                    lesson.setVideo(null);
                    lessonRepository.save(lesson);
                    videoRepository.delete(video);
                }
                
                if (lesson.getQuiz() != null) {
                    Quiz quiz = lesson.getQuiz();
                    lesson.setQuiz(null);
                    lessonRepository.save(lesson);
                    quizService.deleteQuiz(quiz.getId());
                }
            }
            
            // Lưu lesson
            lessonRepository.save(lesson);
            
            // Cập nhật tổng số lesson và duration cho course
            updateCourseTotals(lesson.getChapter().getCourse().getCourseId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Lesson updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating lesson: " + e.getMessage());
        }
        
        return "redirect:../createcourse/coursecontent";
    }

    // Helper method để cập nhật tổng số lesson và duration cho course
    private void updateCourseTotals(Long courseId) {
        Course course = courseService.findCourseById(courseId);
        if (course != null) {
            int totalLesson = 0;
            int totalDuration = 0;
            
            List<Chapter> chapters = chapterService.chapterListByCourse(courseId);
            for (Chapter chapter : chapters) {
                List<Lesson> lessons = lessonRepository.findByChapterId(chapter.getChapterId());
                if (lessons != null) {
                    totalLesson += lessons.size();
                    for (Lesson lesson : lessons) {
                        if (lesson.getDuration() != null) {
                            totalDuration += lesson.getDuration();
                        }
                    }
                }
            }

            courseService.saveCourse(course.getCourseId());
        }
    }
    @PostMapping("/createcourse/autofillchapter")
    public String autoFillChapterOrderNumbers(RedirectAttributes redirectAttributes,
                                              HttpServletRequest request) {
        Long courseId = getCourseIdFromCookie(request);
        chapterService.autoFillOrderNumbers(courseId);
        redirectAttributes.addFlashAttribute("successMessage", "Chapter order numbers auto-filled successfully.");
        return "redirect:../createcourse/coursecontent";
    }

    @PostMapping("/createcourse/autofilllesson")
    public String autoFillLessonOrderNumbers(@RequestParam("chapterId") Long chapterId,
                                             RedirectAttributes redirectAttributes) {
        lessonService.autoFillOrderNumbers(chapterId);
        redirectAttributes.addFlashAttribute("successMessage", "Lesson order numbers auto-filled successfully.");
        return "redirect:../createcourse/coursecontent";
    }
}
