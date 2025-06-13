package com.OLearning.service;

import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.lesson.LessonMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.video.VideoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonChapterService {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoRepository videoRepository;
    public Lesson createLesson(LessonVideoDTO lessonVideoDTO) {
        Lesson lesson = lessonMapper.DtoToLesson(lessonVideoDTO);
        Chapter chapter = chapterService.getChapterById(lessonVideoDTO.getChapterId());
        lesson.setChapter(chapter);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
//        Video video = videoService.createVideo(lessonVideoDTO.getVideoUrl());
//        lesson.setVideo(video);
        return lessonRepository.save(lesson);
    }
    @Transactional
    public void deleteChapter(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findByChapterId(chapterId);
        for (Lesson lesson : lessons) {
            lessonService.deleteAllFkByLessonId(lesson.getLessonId());
        }
        chapterRepository.deleteById(chapterId);
    }
    public Lesson createLesson(LessonTitleDTO lessonTitleDTO) {
        Lesson lesson = lessonMapper.LessonTitleDtoToLesson(lessonTitleDTO);
        Chapter chapter = chapterService.getChapterById(lessonTitleDTO.getChapterId());
        lesson.setChapter(chapter);
        return lessonRepository.save(lesson);
    }
}
