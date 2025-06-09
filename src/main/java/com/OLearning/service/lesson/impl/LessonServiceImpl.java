package com.OLearning.service.lesson.impl;

import com.OLearning.dto.lesson.LessonDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Video;
import com.OLearning.mapper.lesson.LessonMapper;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.video.VideoService;
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
    public Lesson createLesson(LessonDTO lessonDTO) {
        Lesson lesson = lessonMapper.DtoToLesson(lessonDTO);
        Chapter chapter = chapterService.getChapterById(lessonDTO.getChapterId());
        lesson.setChapter(chapter);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        Video video = videoService.createVideo(lessonDTO.getVideoUrl());
        lesson.setVideo(video);
        return lessonRepository.save(lesson);
    }

    @Override
    public List<Lesson> findLessonsByChapterId(Long chapterId) {
        return lessonRepository.findByChapterId(chapterId);
    }
}
