package com.OLearning.controller.api;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.OLearning.entity.LessonCompletion;
import com.OLearning.service.lessonCompletion.LessonCompletionService;

import java.util.List;

@RestController
@RequestMapping("/api/lessonCompletion")
public class LessonCompletionController {
    @Autowired
    private LessonCompletionService lessonCompletionService;

    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<List<LessonCompletionDTO>> getByUserAndCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId) {

        List<LessonCompletionDTO> dtos = lessonCompletionService.getByUserAndCourse(userId, courseId);
        if (dtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);
    }


}
