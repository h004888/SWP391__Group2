package com.OLearning.service;

import com.OLearning.repository.LessonRepository;
import com.OLearning.repository.QuizRepository;
import com.OLearning.repository.VideoRepository;
import com.OLearning.service.quiz.QuizService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonQuizService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizService quizService;
    @Transactional
    public void deleteAllFkByLessonId(Long lessonId) {
        // Delete all videos associated with the lesson
        videoRepository.deleteByLesson_LessonId(lessonId);

        // Delete all quizzes associated with the lesson
        quizService.deleteByLessonId(lessonId);

        // Delete the lesson
        lessonRepository.deleteById(lessonId);
        //bay gio thi xoa bay chapter id thi minh phai tim list lesson trong chapter id do va xoa theo lesson id
    }
}
