package com.OLearning.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadImageFile {
    String uploadImageFile(MultipartFile file) throws IOException;
}
