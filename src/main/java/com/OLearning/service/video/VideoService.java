package com.OLearning.service.video;

import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.Video;

public interface VideoService {
    Video createVideo(String videoUrl);
    Video saveVideo(VideoDTO videoDTO, Long lessonId);
}
