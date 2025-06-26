package com.OLearning.controller.instructorDashBoard;

import com.OLearning.repository.CourseRepository;
import com.OLearning.service.cloudinary.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequestMapping("/api/video")
@RestController
public class JsonController {
    @Autowired
    private UploadFile uploadFile;

    @GetMapping("/signed-url")
    public ResponseEntity<String> getSignedUrl(
            @RequestParam String publicId,
            @RequestParam(defaultValue = "video") String resourceType,
            @RequestParam(defaultValue = "300") int expireSeconds) {
        String url = uploadFile.generateSignedVideoUrl(publicId, expireSeconds, resourceType);
        return ResponseEntity.ok(url);
    }
}
