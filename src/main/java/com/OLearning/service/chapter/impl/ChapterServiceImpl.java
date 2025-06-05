package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashboard.ChapterDTO;
import com.OLearning.entity.Chapters;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lessons;
import com.OLearning.mapper.instructorDashBoard.ChapterMapper;
import com.OLearning.repository.instructorDashBoard.ChapterRepository;
import com.OLearning.service.instructorDashBoard.ChapterService;
import com.OLearning.service.instructorDashBoard.CourseService;
import com.OLearning.service.instructorDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CourseService courseService;
    @Override
    public Chapters saveChapter(ChapterDTO chapterDTO) {
        Chapters chapter = chapterMapper.dtoToChapter(chapterDTO);
        Course course = courseService.findCourseById(chapterDTO.getCourseId());
        chapter.setCourse(course);
         return chapterRepository.save(chapter);
    }

    @Override
    public List<Chapters> chapterListByCourse(Long courserId) {
        return chapterRepository.findChaptersByCourse(courserId);
    }

    @Override
    public Chapters getChapterById(Long id) {
        return chapterRepository.findChapterById(id);
    }
}
