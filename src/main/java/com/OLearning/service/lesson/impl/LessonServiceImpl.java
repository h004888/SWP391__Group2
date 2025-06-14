package com.OLearning.service.lesson.impl;

import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.lesson.LessonMapper;
import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.QuizRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private LessonMapper lessonMapper;

    @Override
    public List<Lesson> findLessonsByChapterId(Long chapterId) {
        return lessonRepository.findByChapterId(chapterId);
    }

    @Override
    public void deleteAllFkByLessonId(Long lessonId) {
        // Delete all videos associated with the lesson
        videoRepository.deleteByLesson_LessonId(lessonId);

        // Delete all quizzes associated with the lesson
        quizRepository.deleteByLesson_LessonId(lessonId);

        // Delete the lesson
        lessonRepository.deleteById(lessonId);
        //bay gio thi xoa bay chapter id thi minh phai tim list lesson trong chapter id do va xoa theo lesson id
    }

    @Override
    public void updateContentType(Long lessonId, String contenType) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        lesson.setContentType(contenType);
        lessonRepository.save(lesson);
    }
}
