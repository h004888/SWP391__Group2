package com.OLearning.service.course;

import com.OLearning.dto.course.*;
import com.OLearning.entity.Course;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    Page<CourseDTO> getAllCourses(Pageable pageable);

    Optional<CourseDetailDTO> getDetailCourse(Long id);

    boolean approveCourse(Long id);

    boolean rejectCourse(Long id);

//    List<CourseDTO> filterCourses(String keyword, Integer categoryId, String price, String status);

    Page<CourseDTO> filterCoursesWithPagination(String keyword, Long category, String price, String status, int page, int size);

    Page<CourseDTO> filterCoursesInstructorManage(Long userId, Long categoryId, String status, String price, String title, int page, int size);

    void deleteCourse(Long courseId);

    Course findCourseById(Long courseId);

    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);

    AddCourseStep1DTO draftCourseStep1(Course course);

    Course submitCourse(Long courseId, String status);

    Page<CourseDTO> findCourseByUserId(Long userId, int page, int size);

    Page<CourseDTO> searchCourse(Long userId, String title, int page, int size);

    Course createCourseMedia(Long courseId, CourseMediaDTO CourseMediaDTO);

    void saveCourse(Long courseId);

    List<CourseDTO> getCourseDTOsByCategoryId(Long categoryId);

    CourseViewDTO getCourseById(Long id);

    List<Chapter> getChaptersWithLessons(Long courseId);

    List<CourseViewDTO> getTopCourses();

    List<CourseViewDTO> getCoursesByCategoryId(int categoryId);

    Page<CourseViewDTO> searchCoursesGrid(
            List<Long> categoryIds,
            List<String> priceFilters,
            List<String> levels,
            String sortBy,
            String keyword,
            int page,
            int size);

    void blockCourse(Long courseId);

    void setPendingBlock(Long courseId);

    Course findById(Long courseId);

    int countByInstructorAndStatus(Long userId, String status);
}
