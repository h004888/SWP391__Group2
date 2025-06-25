package com.OLearning.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFile {
    String uploadImageFile(MultipartFile file) throws IOException;
    String uploadVideoFile(MultipartFile file) throws IOException;
    String generateSignedVideoUrl(String publicId, int expireSeconds, String ResourceType);
}
