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

    @Override
    public List<Lesson> findLessonsByChapterId(Long chapterId) {
        return lessonRepository.findByChapterId(chapterId);
    }



    @Override
    public void updateContentType(Long lessonId, String contenType) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        lesson.setContentType(contenType);
        lessonRepository.save(lesson);
    }

    @Override
    public void autoFillOrderNumbers(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findByChapterId(chapterId);
        int orderNumber = 1;
        for (Lesson lesson : lessons) {
            lesson.setOrderNumber(orderNumber++);
            lessonRepository.save(lesson);
        }
    }
}
