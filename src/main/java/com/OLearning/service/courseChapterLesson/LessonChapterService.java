package com.OLearning.service.courseChapterLesson;

import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.lesson.LessonMapper;
import com.OLearning.repository.ChapterRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.chapter.ChapterService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private LessonMapper lessonMapper;
    @Autowired
    private LessonQuizService lessonQuizService;
    @Transactional
    public void deleteChapter(Long chapterId) {
        List<Lesson> lessons = lessonRepository.findByChapterId(chapterId);
        for (Lesson lesson : lessons) {
            lessonQuizService.deleteAllFkByLessonId(lesson.getLessonId());
        }
        chapterRepository.deleteById(chapterId);
    }
    public Lesson createLesson(LessonTitleDTO lessonTitleDTO) {
        Lesson lesson = lessonMapper.LessonTitleDtoToLesson(lessonTitleDTO);
        Chapter chapter = chapterService.getChapterById(lessonTitleDTO.getChapterId());
        lesson.setChapter(chapter);
        return lessonRepository.save(lesson);
    }
}
