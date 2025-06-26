package com.OLearning.controller.api;

import com.OLearning.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentAPI {
    @Autowired
    private EnrollmentService enrollmentService;
    @GetMapping("/check-access")
    public ResponseEntity<Boolean> checkAccess(
            @RequestParam("userId") Long userId,
            @RequestParam("courseId") Long courseId) {

        boolean hasAccess = enrollmentService.hasEnrolled(userId, courseId);
        return hasAccess
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
