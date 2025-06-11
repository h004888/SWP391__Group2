package com.OLearning.service.cloudinary.impl;

import com.OLearning.service.cloudinary.UploadImageFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadImageFileImpl implements UploadImageFile {
    private final Cloudinary cloudinary; //tu tao contructor de inject Cloudinary bean

    @Override
    public String uploadImageFile(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generetePublicValue(file.getOriginalFilename());
        log.info("publicValue:{}", publicValue);
        String extension = getFileName(file.getOriginalFilename())[1];
        log.info("extension: {}", extension);
        File fileUpload = convert(file);
        log.info("fileUpload: {}", fileUpload);
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        cleanDisk(fileUpload);
        return cloudinary.url().generate(StringUtils.join(publicValue, ".", extension));
    }

    private File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        File convFile = new File(StringUtils.join(generetePublicValue(file.getOriginalFilename()), getFileName(file.getOriginalFilename())[1]));
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, convFile.toPath());
        }
        return convFile;
    }

    public String generetePublicValue(String originalFilename) {
        String fileName = getFileName(originalFilename)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    public String[] getFileName(String originalFilename) {
        return originalFilename.split("\\.");
    }

    private void cleanDisk(File file) {
        try {
            log.info("file: {}", file.getAbsolutePath());
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", file.getName(), e);
        }
    }
}