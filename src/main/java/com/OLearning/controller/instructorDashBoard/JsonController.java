package com.OLearning.controller.instructorDashBoard;

import com.OLearning.service.cloudinary.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/upload")
@RestController
@RequiredArgsConstructor
public class JsonController {
    private final UploadFile uploadFile;

    @PostMapping("/image")
    public String uploadImageFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile.uploadImageFile(file);
    }
    @PostMapping("/video")
    public String uploadVideoFile(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadFile.uploadVideoFile(file);
    }
}
