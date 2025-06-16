package com.OLearning.service;

import com.OLearning.repository.ChapterRepository;
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
    public void deleteCourseFK(Long courseId) {
        chapterService.chapterListByCourse(courseId).forEach(chapter -> {
            lessonChapterService.deleteChapter(chapter.getChapterId());
        });
        courseService.deleteCourse(courseId);
    }
}
