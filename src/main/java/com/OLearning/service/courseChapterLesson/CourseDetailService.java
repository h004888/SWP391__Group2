package com.OLearning.service.courseChapterLesson;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.service.chapter.ChapterService;
import com.OLearning.service.lesson.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseDetailService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonService lessonService;

   public CourseDTO findCourseById(Long courseId) {
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isPresent()) {
                CourseDTO courseDTO = courseMapper.MapCourseDTO(course.get());
                int totalLesson = 0;
                int totalDuration = 0;
                List<Chapter> chapters = chapterService.chapterListByCourse(courseDTO.getCourseId());
                for (Chapter chapter : chapters) {
                    List<Lesson> lessons = lessonService.findLessonsByChapterId(chapter.getChapterId());
                    if (lessons != null && lessons.size() > 0) {
                        totalLesson += lessons.size();
                        for (Lesson lesson : lessons) {
                            if (lesson.getDuration() != null) {
                                totalDuration += lesson.getDuration();
                            }
                        }
                    }
                }
                courseDTO.setTotalLessons(totalLesson);
                courseDTO.setDuration(totalDuration);
                if (course.get().getCategory() != null) {
                    courseDTO.setCategoryName(course.get().getCategory().getName());
                } else {
                    courseDTO.setCategoryName("not found");
                }
                return courseDTO;
            }
            return null;
        }
    }

