package com.OLearning.dto.video;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class VideoDTO {
    Long id;
    MultipartFile videoUrl;
    LocalTime duration;
    Long lessonId;
    
    // Temporary field for frontend compatibility (duration in minutes)
    private Integer durationMinutes;
    
    // Getter and setter for duration in minutes
    public Integer getDurationMinutes() {
        if (duration != null) {
            return duration.getHour() * 60 + duration.getMinute();
        }
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        if (durationMinutes != null) {
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            this.duration = LocalTime.of(hours, minutes);
        }
    }
}
