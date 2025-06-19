package com.OLearning.service.lessonCompletion.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.entity.LessonCompletion;
import com.OLearning.repository.LessonCompletionRepository;
import com.OLearning.service.lessonCompletion.LessonCompletionService;

@Service
public class LessonCompletionServiceImpl implements LessonCompletionService {
    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;

    @Override
    public List<Long> getCompletedLessonIdsByUser(Long userId) {
        return lessonCompletionRepository.findByUser_UserId(userId)
                .stream()
                .map(lc -> lc.getLesson().getLessonId())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLessonCompleted(Long userId, Long lessonId) {
        return lessonCompletionRepository.existsByUser_UserIdAndLesson_LessonId(userId, lessonId);
    }

    @Override
    public int countCompletedLessons(Long userId, Long courseId) {
        return lessonCompletionRepository.countCompletedLessons(userId, courseId);
    }

}
