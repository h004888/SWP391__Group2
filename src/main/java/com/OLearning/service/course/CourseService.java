package com.OLearning.service.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.OLearning.entity.Chapter;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

        Course getCourseById(Long id);
    Page<CourseDTO> getAllCourses(Pageable pageable);

        List<Chapter> getChaptersWithLessons(Long courseId);

        List<Course> getTopCourses();

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean approveCourse(Long id);

    boolean rejectCourse(Long id);


//    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status);

    Page<CourseDTO> filterCoursesWithPagination(String keyword, Long category, String price, String status, int page, int size);

    Page<CourseDTO> filterCoursesInstructorManage(Long userId, Long categoryId, String status, String price, int page, int size);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);

    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);

        Page<CourseDTO> searchCoursesGrid(
                        List<Long> categoryIds,
                        List<String> priceFilters,
                        List<String> levels,
                        String sortBy,
                        String keyword,
                        int page,
                        int size);

    AddCourseStep1DTO draftCourseStep1(Course course);

    Course submitCourse(Long courseId, String status);

    Page<CourseDTO> findCourseByUserId(Long userId, int page, int size);

    Page<CourseDTO> searchCourse(Long userId, String title, int page, int size);
    Course createCourseMedia(Long courseId, CourseMediaDTO CourseMediaDTO);

    void saveCourse(Long courseId);

    List<CourseDTO> getCourseDTOsByCategoryId(Long categoryId);
}
