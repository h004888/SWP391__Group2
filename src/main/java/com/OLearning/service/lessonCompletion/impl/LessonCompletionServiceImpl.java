package com.OLearning.service.lessonCompletion.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.OLearning.entity.LessonCompletion;
import com.OLearning.repository.LessonCompletionRepository;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;
import com.OLearning.service.user.UserService;

@Service
@RequiredArgsConstructor
public class LessonCompletionServiceImpl implements LessonCompletionService {
    @Autowired
    private LessonCompletionRepository lessonCompletionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LessonService lessonService;

    @Override
    public List<LessonCompletionDTO> getByUserAndCourse(Long userId, Long courseId) {
        return lessonCompletionRepository.findByUser_UserIdAndLesson_Chapter_Course_CourseId(userId, courseId).stream()
                .map(lc -> new LessonCompletionDTO(
                        lc.getCompletionId(),
                        lc.getUser().getUserId(),
                        lc.getLesson().getLessonId(),
                        lc.getCompletedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkLessonCompletion(Long userId, Long lessonId) {
        return lessonCompletionRepository.existsByUserUserIdAndLessonLessonId(userId, lessonId);
    }

    @Override
    public void markLessonAsCompleted(Long userId, Long lessonId) {
        if (!checkLessonCompletion(userId, lessonId)) {
            LessonCompletion completion = new LessonCompletion();
            completion.setLesson(lessonService.findById(lessonId).orElseThrow());
            completion.setUser(userService.findById(userId).orElseThrow());
            completion.setCompletedAt(LocalDateTime.now());
            lessonCompletionRepository.save(completion);
        }
    }

    @Override
    public Double getOverallProgressOfUser(Long userId, Long courseId) {
        return lessonCompletionRepository.getCourseProgressPercent(userId, courseId);
    }

    @Override
    public Integer getNumberOfCompletedLessons(Long userId, Long courseId) {
        return lessonCompletionRepository.getNumberOfCompletedLessons(userId, courseId);
    }

}
