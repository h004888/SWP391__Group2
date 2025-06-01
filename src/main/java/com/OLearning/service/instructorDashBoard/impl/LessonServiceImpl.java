package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashboard.LessonDTO;
import com.OLearning.entity.Chapters;
import com.OLearning.entity.Lessons;
import com.OLearning.entity.Video;
import com.OLearning.mapper.instructorDashBoard.LessonMapper;
import com.OLearning.repository.instructorDashBoard.LessonRepository;
import com.OLearning.repository.instructorDashBoard.VideoRepository;
import com.OLearning.service.instructorDashBoard.ChapterService;
import com.OLearning.service.instructorDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private VideoRepository videoRepository;
    @Override
    public Lessons createLesson(LessonDTO lessonDTO) {
        Lessons lesson = lessonMapper.DtoToLesson(lessonDTO);
        Chapters chapter = chapterService.getChapterById(lessonDTO.getChapterId());
        lesson.setChapter(chapter);
        Video video = new Video();
        video.setVideoUrl(lessonDTO.getVideoUrl());
        video.setUploadDate(LocalDateTime.now());
        lesson.setVideo(video);
        videoRepository.save(video);
        return lessonRepository.save(lesson);
    }
}
