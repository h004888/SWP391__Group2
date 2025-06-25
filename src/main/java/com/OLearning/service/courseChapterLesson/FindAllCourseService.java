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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FindAllCourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private LessonService lessonService;
    public Page<CourseDTO> findCourseByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findByInstructorUserId(userId, pageable);//Page<Course> la doi tuong chua ca danh sach khoa hoc
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : coursePage.getContent()) {
            CourseDTO courseDTO = courseMapper.MapCourseDTO(course);
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
            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("not found");
            }
            courseDTOList.add(courseDTO);
        }
        return new PageImpl<>(courseDTOList, pageable, coursePage.getTotalElements());
    }
}
