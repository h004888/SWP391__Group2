package com.OLearning.service.instructorDashboard.impl;

import com.OLearning.dto.instructorDashboard.ChapterDTO;
import com.OLearning.entity.Chapters;
import com.OLearning.entity.Course;
import com.OLearning.mapper.instructorDashBoard.ChapterMapper;
import com.OLearning.repository.instructorDashBoard.InstructorCourseRepo;
import com.OLearning.repository.instructorDashBoard.ChapterRepository;
import com.OLearning.service.instructorDashboard.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private InstructorCourseRepo instructorCourseRepo;
    @Autowired
    private ChapterMapper chapterMapper;
    @Override
    public Chapters saveChapter(ChapterDTO chapterDTO) {
        Chapters chapter = chapterMapper.dtoToChapter(chapterDTO);
        Course course = instructorCourseRepo.findById(chapterDTO.getCourseId()).get();
        chapter.setCourse(course);
        return chapterRepository.save(chapter);
    }
}
