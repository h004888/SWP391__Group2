package com.OLearning.service.chapter.impl;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.mapper.chapter.ChapterMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {
    private static final Logger logger = LoggerFactory.getLogger(ChapterServiceImpl.class);
    
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CourseService courseService;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public Chapter saveChapter(ChapterDTO chapterDTO) {
        logger.info("Saving chapter with title: {}", chapterDTO.getTitle());
        logger.info("Course ID: {}", chapterDTO.getCourseId());
        
        Chapter chapter = chapterMapper.dtoToChapter(chapterDTO);
        logger.info("Mapped chapter entity: {}", chapter);
        
        Course course = courseService.findCourseById(chapterDTO.getCourseId());
        if (course == null) {
            logger.error("Course not found with ID: {}", chapterDTO.getCourseId());
            throw new RuntimeException("Course not found");
        }
        logger.info("Found course: {}", course.getCourseId());
        
        chapter.setCourse(course);
        Chapter savedChapter = chapterRepository.save(chapter);
        logger.info("Saved chapter with ID: {}", savedChapter.getChapterId());
        
        return savedChapter;
    }

    @Override
    public List<Chapter> chapterListByCourse(Long courserId) {
        return chapterRepository.findChaptersByCourse(courserId);
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterRepository.findChapterById(id);
    }

    @Override
    public void updateChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    @Override
    public void autoFillOrderNumbers(Long courseId) {
        List<Chapter> chapters = chapterRepository.findChaptersByCourse(courseId);
        int orderNumber = 1;
        for (Chapter chapter : chapters) {
            chapter.setOrderNumber(orderNumber++);
            chapterRepository.save(chapter);
        }
    }
}

