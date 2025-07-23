package com.OLearning.service.enrollment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.OLearning.dto.enrollment.UserCourseProgressDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.dto.enrollment.EnrollmentDTO;
import org.springframework.data.domain.Page;
import java.util.Map;

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

    Long countEnrollmentsByInstructorAndDateRange(long instructorId, java.time.LocalDate start,
            java.time.LocalDate end);

    Page<EnrollmentDTO> getEnrollmentsByInstructorId(Long userId, int page, int size);

    boolean blockEnrollment(int enrollmentId);

    EnrollmentDTO getRequestById(int enrollmentId);

    Integer getTotalEnrollment(Long courseId);

    Page<EnrollmentDTO> getEnrollmentByCourseId(Long courseId, int page, int size);

    Integer getWeeksEnrolled(Long userId, Long courseId);

    void updateProgressByUser(Long userId, Long courseId);

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    List<Enrollment> findByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course);

    Optional<Enrollment> findFirstByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course);

    Long countByUserAndCourse(User user, Course course);

    List<Enrollment> findAllByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course);

    void cleanupDuplicateEnrollments(User user, Course course);

    // Lấy enrollment trong 30 ngày gần nhất
    List<Enrollment> findByEnrollmentDateAfter(LocalDate date);
    List<Long> getTotalEnrollmentOfInstructor(List<UserDTO> instructors);
    // Lấy enrollment của một user cụ thể
    List<Enrollment> findByUserId(Long userId);

    List<UserCourseProgressDTO> getProgressCoursesByUserId(Long userId);

    int updateStatusToCompleted(Long userId, Long courseId);

}
