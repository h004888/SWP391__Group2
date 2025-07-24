package com.OLearning.service.enrollment;

import java.util.List;

import com.OLearning.dto.enrollment.EnrollMaxMinDTO;
import org.springframework.stereotype.Service;

import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;

import com.OLearning.dto.enrollment.EnrollmentDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Map;
import com.OLearning.dto.enrollment.EnrollmentFilterDTO;

@Service
public interface EnrollmentService {
    List<Course> getCoursesByUserId(Long userId);
    boolean hasEnrolled(Long userId, Long courseId);
    boolean unblockEnrollment(int enrollmentId);
    int enrollmentCount();
    Map<String, Long> getEnrollmentsByCategoryAndDateRange(LocalDate startDate, LocalDate endDate);
    Long getTotalStudents();
    Enrollment getEnrollmentById(int erollmentId);
    Long getCompletedEnrollments();
    Long getStudentCountByInstructorId(Long instructorId);
    // Đếm số lượng enrollment theo instructor, năm, tháng
    Long countEnrollmentsByInstructorAndMonth(long instructorId, int year, int month);
    Long countEnrollmentsByInstructorAndDateRange(long instructorId, java.time.LocalDate start, java.time.LocalDate end);
    Page<EnrollmentDTO> getEnrollmentsByInstructorId(Long userId, int page, int size);
    boolean blockEnrollment(int enrollmentId);
    EnrollmentDTO getRequestById(int enrollmentId);
    Integer getTotalEnrollment(Long courseId);
    Page<EnrollmentDTO> getEnrollmentByCourseId(Long courseId, int page, int size);
    Integer countTotalEnrollment(Long instructorId);
    Integer countTotalEnrollmentOnGoing(Long instructorId);
    Integer countTotalEnrollmentCompelted(Long instructorId);
    EnrollMaxMinDTO enrollmentMax(Long instructorId);
    EnrollMaxMinDTO enrollmentMin(Long instructorId);
    Integer completeMax(Long instructorId);
    Integer onGoingMax(Long instructorId);
    Integer completeMin(Long instructorId);
    Integer onGoingMin(Long instructorId);
    Page<EnrollmentDTO> findEnrollmentsWithFilters(Long instructorId, EnrollmentFilterDTO filterDTO);
    
    List<Course> getInstructorCourses(Long instructorId);
}
