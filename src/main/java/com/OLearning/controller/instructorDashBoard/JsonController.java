package com.OLearning.controller.instructorDashBoard;

import com.OLearning.repository.CourseRepository;
import com.OLearning.service.cloudinary.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/testpage")
@RestController
@RequiredArgsConstructor
public class JsonController {
    private final UploadFile uploadFile;
    private final CourseRepository courseRepository;

    //lay theo id
    //dung pathvariable\
    //dung request param de tim theo s
    @GetMapping("/{id}")
    public String getCourseById(@PathVariable Long id) {
        return courseRepository.findById(id).get().getTitle();
    }

    @PostMapping("/image")
    public String uploadImageFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile.uploadImageFile(file);
    }
    @PostMapping("/video")
    public String uploadVideoFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile.uploadVideoFile(file);
    }

}
