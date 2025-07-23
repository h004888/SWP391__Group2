package com.OLearning.mapper.video;

import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.Video;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class VideoMapper {
    public Video VideoMapper(VideoDTO videoDTO) {
        Video video = new Video();
        if (videoDTO.getId() != null) {
            video.setId(videoDTO.getId());
        }
        video.setVideoUrl(videoDTO.getVideoUrl().getOriginalFilename());
        video.setDuration(videoDTO.getDuration());
        video.setUploadDate(LocalDateTime.now());
        return video;
    }
}
