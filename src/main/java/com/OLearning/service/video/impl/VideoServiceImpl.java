package com.OLearning.service.video.impl;

import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Video;
import com.OLearning.mapper.video.VideoMapper;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private LessonRepository lessonRepository;
 //video bang iframe
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
//video bang file to cloudinary
    @Override
    public Video saveVideo(VideoDTO videoDTO, Long lessonId) {
        videoRepository.findByLesson_LessonId(lessonId).ifPresent(v -> videoRepository.delete(v));
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        Video video = new Video();
        video = videoMapper.VideoMapper(videoDTO);
        video.setLesson(lesson);
        try {
            if (videoDTO.getVideoUrl() != null && !videoDTO.getVideoUrl().isEmpty()) {
                String videoUUID = uploadFile.uploadVideoFile(videoDTO.getVideoUrl());
                video.setVideoUrl(videoUUID);
            }
            return videoRepository.save(video);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload course media", e);
        }
    }
}
