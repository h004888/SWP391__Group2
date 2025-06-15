package com.OLearning.dto.video;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class VideoDTO {

    MultipartFile videoUrl;
    Integer duration;
    Long lessonId;
}
