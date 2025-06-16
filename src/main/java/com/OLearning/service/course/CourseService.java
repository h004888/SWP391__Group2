package com.OLearning.service.course;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Course;
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

    Page<CourseDTO> filterCoursesWithPagination(String keyword, Integer category, String price, String status, int page, int size);
    void deleteCourse(Long courseId);
    Course findCourseById(Long courseId);
    Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO);
    AddCourseStep1DTO draftCourseStep1(Course course);
    Course submitCourse(Long courseId, String status);
    Page<CourseDTO> findCourseByUserId(Long userId, int page, int size);
    Page<CourseDTO> searchCourse(Long userId, String title, int page, int size);
    Course createCourseMedia(Long courseId, CourseMediaDTO CourseMediaDTO);
    void saveCourse(Long courseId);
    Page<CourseDTO> filterCourseByCategoryName(String categoryName, Long userId, int page, int size);
    //list bao gom course
    //page no lai bao gon list, moi page la 1 list

}
