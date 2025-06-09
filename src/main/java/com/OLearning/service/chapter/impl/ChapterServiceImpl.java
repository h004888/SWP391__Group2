package com.OLearning.service.chapter.impl;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.mapper.chapter.ChapterMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CourseService courseService;
    @Override
    public Chapter saveChapter(ChapterDTO chapterDTO) {
        Chapter chapter = chapterMapper.dtoToChapter(chapterDTO);
        Course course = courseService.findCourseById(chapterDTO.getCourseId());
        chapter.setCourse(course);
         return chapterRepository.save(chapter);
    }

    @Override
    public List<Chapter> chapterListByCourse(Long courserId) {
        return chapterRepository.findChaptersByCourse(courserId);
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterRepository.findChapterById(id);
    }
}
