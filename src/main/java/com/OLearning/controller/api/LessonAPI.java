package com.OLearning.controller.api;

import com.OLearning.entity.Lesson;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lesson")
public class LessonAPI {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonCompletionService lessonCompletionService;
    @Autowired
    private EnrollmentService enrollmentService;
//
//    @GetMapping("/next")
//    public ResponseEntity<Lesson> getNextLesson(
//            @RequestParam("userId")   Long userId,
//            @RequestParam("courseId") Long courseId) {
//
//        Optional<Lesson> next = lessonService.getNextLesson(userId, courseId);
//        return next
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.noContent().build());
//    }
    @GetMapping("/first")
    public ResponseEntity<Lesson> getFirstLesson(
            @RequestParam("courseId") Long courseId) {

        Lesson first = lessonService.findFirstLesson(courseId);
        return first != null
                ? ResponseEntity.ok(first)
                : ResponseEntity.noContent().build();
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

    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<?> markLessonComplete(
            @RequestParam Long lessonId,
            @RequestParam Long courseId,
            Principal principal
    ) {
        User user = extractCurrentUser(principal);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!lessonCompletionService.checkLessonCompletion(user.getUserId(), lessonId)) {
            lessonCompletionService.markLessonAsCompleted(user.getUserId(), lessonId);
            enrollmentService.updateProgressByUser(user.getUserId(), courseId);
        }

       Lesson nextLesson = lessonService.getNextLesson(courseId, lessonId);
        if (nextLesson!=null) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "nextLessonUrl", "/learning/course/" + courseId + "/lesson/" + nextLesson.getLessonId()
            ));
        } else {
            return ResponseEntity.ok(Map.of("status", "completed_all"));
        }
    }
}
