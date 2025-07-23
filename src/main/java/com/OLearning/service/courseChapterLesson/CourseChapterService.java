package com.OLearning.service.courseChapterLesson;

import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseChapterService {
    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonChapterService lessonChapterService;
    @Autowired
    private com.OLearning.repository.EnrollmentRepository enrollmentRepository;
    @Autowired
    private com.OLearning.repository.OrdersRepository ordersRepository;

    public void deleteCourseFK(Long courseId) {
        if (!enrollmentRepository.findByCourseId(courseId).isEmpty()) {
            throw new RuntimeException("There are students enrolled in this course. Deletion is not allowed.");
        }
        ordersRepository.findAll().forEach(order -> {
            boolean hasCourse = order.getOrderDetails().stream()
                .anyMatch(od -> od.getCourse() != null && od.getCourse().getCourseId().equals(courseId));
            if (hasCourse) {
                ordersRepository.delete(order);
            }
        });
        chapterService.chapterListByCourse(courseId).forEach(chapter -> {
            lessonChapterService.deleteChapter(chapter.getChapterId());
        });
        courseService.deleteCourse(courseId);
    }
}
