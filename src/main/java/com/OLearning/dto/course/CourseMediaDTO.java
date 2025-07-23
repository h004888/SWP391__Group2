package com.OLearning.dto.course;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class CourseMediaDTO {
    private MultipartFile image;
    private MultipartFile video;
    private Long courseId;
}
