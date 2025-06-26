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
    Long id;
    MultipartFile videoUrl;
    Integer duration;
    Long lessonId;
    
    // Temporary field for frontend compatibility (duration in minutes)
    private Integer durationMinutes;
    
    // Getter and setter for duration in minutes
    public Integer getDurationMinutes() {
        if (duration != null) {
            return duration;
        }
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        this.duration = durationMinutes;
    }
}
