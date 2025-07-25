package com.OLearning.service.cloudinary.impl;
import com.OLearning.service.cloudinary.UploadFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UploadFileImpl implements UploadFile {
    private final Cloudinary cloudinary;

    @Override
    public String uploadImageFile(MultipartFile file) throws IOException {
        return uploadFile(file, "image");
    }
    @Override
    public String uploadVideoFile(MultipartFile file) throws IOException {
        return uploadVideoSeFile(file);
    }

    private String uploadFile(MultipartFile file, String resourceType) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFileName(file.getOriginalFilename())[1];//jpg, png
        File fileUpload = convert(file, publicValue, extension);
        try {
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap(
                            "public_id", publicValue,
                            "resource_type", resourceType
                    )
            );
            return cloudinary.url().resourceType(resourceType).generate(publicValue + "." + extension);
        } finally {
            cleanDisk(fileUpload);
        }
    }
//upload return video duoi dang publicId
    private String uploadVideoSeFile(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        String originalFilename = file.getOriginalFilename();
        String fileNameWithoutExt = getFileName(originalFilename)[0];
        String extension = getFileName(originalFilename)[1];
        String publicValue = UUID.randomUUID().toString() + "_" + fileNameWithoutExt;

        File fileUpload = convert(file, publicValue.replace("." + extension, ""), extension);
        try {
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap(
                            "public_id", publicValue,
                            "resource_type", "video",
                            "type", "private"
                    )
            );
            return publicValue;
        } finally {
            cleanDisk(fileUpload);
        }
    }

    private File convert(MultipartFile file, String publicValue, String extension) throws IOException {
        File convFile = new File(publicValue + "." + extension);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, convFile.toPath());
        }
        return convFile;
    }

    public String generatePublicValue(String originalFilename) {
        String fileName = getFileName(originalFilename)[0];
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    public String[] getFileName(String originalFilename) {
        return originalFilename.split("\\.");
    }

    private void cleanDisk(File file) {
        try {
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", file.getName(), e);
        }
    }
    //generate tu publicId sang Signed de co the phat
    @Override
    public String generateSignedVideoUrl(String publicId, String resourceType) {
        // Không tự thêm extension, chỉ trả về đúng publicId như lưu trên Cloudinary
        return cloudinary.url()
                .resourceType(resourceType)
                .type("private")
                .signed(true)
                .generate(publicId);
    }
}