package com.OLearning.controller.homePage;

import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Controller
public class LessonController {

    @Autowired
    private LessonService lessonService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/lesson/player/{lessonId}")
    public String lessonPlayer(Model model, @PathVariable("lessonId") Long lessonId, Principal principal,
            @RequestParam(value = "courseId", required = true) Long courseId) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = extractCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Course course = courseService.getCourseById(courseId);
        Lesson currentLesson = lessonService.findLessonById(lessonId);

        // --- LOGIC ĐỂ TÌM BÀI HỌC TRƯỚC VÀ BÀI HỌC TIẾP THEO ---

        // 1. Tạo một danh sách phẳng (flat list) chứa tất cả các bài học của khóa học
        List<Lesson> allLessonsInOrder = new ArrayList<>();
        if (course.getListOfChapters() != null) {
            List<Chapter> sortedChapters = new ArrayList<>(course.getListOfChapters());
            sortedChapters.sort(Comparator.comparing(Chapter::getOrderNumber));
            for (Chapter chapter : sortedChapters) {
                if (chapter.getLessons() != null) {
                    List<Lesson> sortedLessons = new ArrayList<>(chapter.getLessons());
                    sortedLessons.sort(Comparator.comparing(Lesson::getOrderNumber));
                    allLessonsInOrder.addAll(sortedLessons);
                }
            }
        }

        // 2. Tìm chỉ số (index) của bài học hiện tại trong danh sách phẳng
        int currentIndex = -1;
        for (int i = 0; i < allLessonsInOrder.size(); i++) {
            if (allLessonsInOrder.get(i).getLessonId().equals(lessonId)) {
                currentIndex = i;
                break;
            }
        }

        // 3. Dựa vào chỉ số, xác định ID của bài học trước và bài học sau
        if (currentIndex != -1) {
            // Thêm ID bài học trước đó vào model nếu nó không phải là bài đầu tiên
            if (currentIndex > 0) {
                model.addAttribute("previousLessonId", allLessonsInOrder.get(currentIndex - 1).getLessonId());
            }
            // Thêm ID bài học tiếp theo vào model nếu nó không phải là bài cuối cùng
            if (currentIndex < allLessonsInOrder.size() - 1) {
                model.addAttribute("nextLessonId", allLessonsInOrder.get(currentIndex + 1).getLessonId());
            }
        }
        // --- KẾT THÚC LOGIC ---

        model.addAttribute("currentLesson", currentLesson);
        model.addAttribute("course", course);
        model.addAttribute("chapters", course.getListOfChapters()); // Vẫn giữ lại để hiển thị sidebar

        return "UserPage/lessonVideo";
    }

    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }
}