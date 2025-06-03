package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.entity.Video;
import com.OLearning.repository.instructorDashBoard.VideoRepository;
import com.OLearning.service.instructorDashBoard.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Override
    public Video createVideo(String videoUrl) {
        Video video = new Video();
        if (videoUrl != null && videoUrl.contains("v=")) {
            int index = videoUrl.indexOf("v=") + 2;//tim vi tri dau tien
            int ampIndex = videoUrl.indexOf("&", index); //tim ki tu cuoi cung trong link
            if (ampIndex > 0) {
                video.setVideoUrl(videoUrl.substring(index, ampIndex));
            } else {
                video.setVideoUrl(videoUrl.substring(index));
            }
        }
        else {
            video.setVideoUrl(videoUrl);
        }
        video.setUploadDate(LocalDateTime.now());
        return videoRepository.save(video);
    }
}
