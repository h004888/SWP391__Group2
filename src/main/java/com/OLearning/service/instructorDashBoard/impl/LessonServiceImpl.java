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
import com.OLearning.service.instructorDashBoard.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private VideoService videoService;
    @Override
    public Lessons createLesson(LessonDTO lessonDTO) {
        Lessons lesson = lessonMapper.DtoToLesson(lessonDTO);
        Chapters chapter = chapterService.getChapterById(lessonDTO.getChapterId());
        lesson.setChapter(chapter);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        Video video = videoService.createVideo(lessonDTO.getVideoUrl());
        lesson.setVideo(video);
        return lessonRepository.save(lesson);
    }

    @Override
    public List<Lessons> findLessonsByChapterId(Long chapterId) {
        return lessonRepository.findByChapterId(chapterId);
    }
}
