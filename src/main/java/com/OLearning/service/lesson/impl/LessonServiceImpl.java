package com.OLearning.service.lesson.impl;

import com.OLearning.entity.Lesson;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.lesson.LessonService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public List<Lesson> findAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public Lesson findLessonById(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Lesson not found with id " + id));
    }

    @Override
    public Lesson saveLesson(Lesson lesson) {
        if (lesson.getCreatedAt() == null) {
            lesson.setCreatedAt(LocalDateTime.now());
        }
        lesson.setUpdatedAt(LocalDateTime.now());
        return lessonRepository.save(lesson);
    }

    @Override
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    @Override
    public Lesson updateLesson(Long id, Lesson lessonDetails) {
        Optional<Lesson> existingLessonOptional = lessonRepository.findById(id);
        if (existingLessonOptional.isPresent()) {
            Lesson existingLesson = existingLessonOptional.get();
            existingLesson.setTitle(lessonDetails.getTitle());
            existingLesson.setDescription(lessonDetails.getDescription());
            existingLesson.setContentType(lessonDetails.getContentType());
            existingLesson.setOrderNumber(lessonDetails.getOrderNumber());
            existingLesson.setIsFree(lessonDetails.getIsFree());
            existingLesson.setDuration(lessonDetails.getDuration());
            existingLesson.setChapter(lessonDetails.getChapter());
            // if needed
            existingLesson.setUpdatedAt(LocalDateTime.now());
            return lessonRepository.save(existingLesson);
        } else {
            throw new RuntimeException("Lesson not found with id " + id);
        }
    }

    @Override
    public Lesson getCurrentLearningLesson(Long userId, Long courseId) {
        List<Lesson> completedLessons = lessonRepository.findCompletedLessons(userId, courseId);

        if (!completedLessons.isEmpty()) {
            Lesson lastLesson = completedLessons.get(0);
            return lessonRepository.findByCourseIdAndOrderNumber(courseId, lastLesson.getOrderNumber() + 1)
                    .orElse(lastLesson);
        } else {
            return lessonRepository.findFirstLesson(courseId);
        }
    }

    @Override
    public int countLessonsInCourse(Long courseId) {
        return lessonRepository.countLessonsByCourseId(courseId);
    }

    @Override
    public boolean existsById(Long id) {
        return lessonRepository.existsById(id);
    }

    @Override
    public Optional<Lesson> findById(Long id) {
        return lessonRepository.findById(id);
    }

    @Override
    public Lesson getNextLesson( Long courseId,Long lessonId) {

        return lessonRepository.findNextLesson(courseId, lessonId).orElse(null);
    }

    @Override
    public Lesson findFirstLesson(Long courseId) {
        return lessonRepository.findFirstByChapter_Course_CourseIdOrderByChapter_ChapterIdAscOrderNumberAsc(courseId).orElse(null);
    }

    @Override
    public Optional<Lesson> getNextLessonAfterCompleted(Long userId, Long courseId) {
        Pageable firstResult = PageRequest.of(0, 1); // chỉ lấy bài đầu tiên chưa học
        return lessonRepository.findNextUncompletedLessonForUser(userId, courseId, firstResult)
                .stream()
                .findFirst();
    }

    @Override
    public Lesson getNextLessonAfterCurrent(Long currentLessonId, Long courseId) {
        return lessonRepository.findNextLessonAfterCurrent(currentLessonId,courseId);
    }

}