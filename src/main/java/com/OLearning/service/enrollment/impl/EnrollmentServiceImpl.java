package com.OLearning.service.enrollment.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.OLearning.service.course.CourseService;
import com.OLearning.dto.enrollment.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.OLearning.dto.enrollment.UserCourseProgressDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.mapper.enrollment.EnrollmentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentMapper mapper;
    @Autowired
    private EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    @Override
    public int enrollmentCount() {
        return (int) enrollmentRepository.count();
    }

    @Override
    public Map<String, Long> getEnrollmentsByCategoryAndDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> enrollmentsByCategory = new HashMap<>();
        List<Object[]> results = enrollmentRepository.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            enrollmentsByCategory.put(category, count);
        }
        return enrollmentsByCategory;
    }

    @Override
    public Long getTotalStudents() {
        return enrollmentRepository.countTotalStudents();
    }

    @Override
    public Enrollment getEnrollmentById(int erollmentId) {
        Optional<Enrollment> enrollment = enrollmentRepository.findByEnrollmentId(erollmentId);
        if (enrollment.isPresent()) {
            return enrollment.get();
        }
        return null;
    }

    @Override
    public Long getCompletedEnrollments() {
        return enrollmentRepository.countCompletedEnrollments();
    }

    @Override
    public Long getStudentCountByInstructorId(Long instructorId) {
        return enrollmentRepository.countStudentsByInstructorId(instructorId);
    }

    @Override
    public Long countEnrollmentsByInstructorAndMonth(long instructorId, int year, int month) {
        return enrollmentRepository.countByInstructorIdAndMonth(instructorId, year, month);
    }

    @Override
    public Long countEnrollmentsByInstructorAndDateRange(long instructorId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        Date startDate = Date.from(startDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        return enrollmentRepository.countByInstructorIdAndDateRange(instructorId, startDate, endDate);
    }

    @Override
    public PageImpl<EnrollmentDTO> getEnrollmentsByInstructorId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByInstructorId(userId, pageable);
        List<EnrollmentDTO> enrollmentDTOList = new ArrayList<>();
        for (Enrollment enrollment : enrollments.getContent()) {
            EnrollmentDTO EnrollmentDTO = mapper.MapEnrollmentDTO(enrollment);
            enrollmentDTOList.add(EnrollmentDTO);
        }
        return new PageImpl<>(enrollmentDTOList, pageable, enrollments.getTotalElements());
    }


    @Override
    public boolean blockEnrollment(int enrollmentId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setStatus("blocked");
            enrollmentRepository.save(enrollment);

            // Gửi email thông báo cho học viên
            try {
                User student = enrollment.getUser();
                Course course = enrollment.getCourse();
                emailService.sendEnrollmentBlockedEmail(student, course);
            } catch (Exception e) {
                log.error("Failed to send enrollment blocked email", e);
                // Không throw exception để không ảnh hưởng đến việc block
            }

            return true;
        }

        return false;
    }

    @Override
    public EnrollmentDTO getRequestById(int enrollmentId) {
        return enrollmentRepository.findByEnrollmentId(enrollmentId).map(mapper::MapEnrollmentDTO)
                .orElseThrow(() -> new RuntimeException("Enrollment request not found with id: " + enrollmentId));
    }

    @Override
    public Integer getTotalEnrollment(Long courseId) {

        Integer count = enrollmentRepository.findByCourseId(courseId).size();
        if(count == null){
            return 0;
        }
        return count;
    }

    @Override
    public Page<EnrollmentDTO> getEnrollmentByCourseId(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByCourseId(courseId, pageable);
        List<EnrollmentDTO> enrollmentDTOList = new ArrayList<>();
        for (Enrollment enrollment : enrollments.getContent()) {
            EnrollmentDTO EnrollmentDTO = mapper.MapEnrollmentDTO(enrollment);
            enrollmentDTOList.add(EnrollmentDTO);
        }
        return new PageImpl<>(enrollmentDTOList, pageable, enrollments.getTotalElements());
    }

    @Override
    public boolean unblockEnrollment(int enrollmentId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            enrollment.setStatus("active"); // Hoặc trạng thái mặc định của bạn
            enrollmentRepository.save(enrollment);

            // Gửi email thông báo cho học viên
            try {
                User student = enrollment.getUser();
                Course course = enrollment.getCourse();
                emailService.sendEnrollmentUnblockedEmail(student, course);
            } catch (Exception e) {
                log.error("Failed to send enrollment unblocked email", e);
                // Không throw exception để không ảnh hưởng đến việc unblock
            }

            return true;
        }

        return false;
    }

    @Override
    public List<Course> getCoursesByUserId(Long userId) {
        return enrollmentRepository.findByUserUserId(userId)
                .stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    @Override
    public Integer getWeeksEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.getWeeksEnrolled(userId, courseId);
    }

    @Override
    public void updateProgressByUser(Long userId, Long courseId) {
        enrollmentRepository.updateProgressByUser(userId, courseId);
    }

    @Override
    public Optional<Enrollment> findByUserAndCourse(User user, Course course) {
        return enrollmentRepository.findFirstByUserAndCourseOrderByEnrollmentDateDesc(user, course);
    }

    @Override
    public List<Enrollment> findByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course) {
        return enrollmentRepository.findByUserAndCourseOrderByEnrollmentDateDesc(user, course);
    }

    @Override
    public Optional<Enrollment> findFirstByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course) {
        return enrollmentRepository.findFirstByUserAndCourseOrderByEnrollmentDateDesc(user, course);
    }

    @Override
    public Long countByUserAndCourse(User user, Course course) {
        return enrollmentRepository.countByUserAndCourse(user, course);
    }

    @Override
    public List<Enrollment> findAllByUserAndCourseOrderByEnrollmentDateDesc(User user, Course course) {
        return enrollmentRepository.findAllByUserAndCourseOrderByEnrollmentDateDesc(user, course);
    }

    @Override
    public void cleanupDuplicateEnrollments(User user, Course course) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByUserAndCourseOrderByEnrollmentDateDesc(user, course);
        if (enrollments.size() > 1) {
            // Giữ lại enrollment đầu tiên (mới nhất), xóa các enrollment còn lại
            for (int i = 1; i < enrollments.size(); i++) {
                enrollmentRepository.delete(enrollments.get(i));
            }
        }
    }

    @Override
    public List<Enrollment> findByEnrollmentDateAfter(LocalDate date) {
        return enrollmentRepository.findByEnrollmentDateAfter(date).stream().toList();
    }

    @Override
    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUser_UserId(userId).stream().toList();
    }

    @Override
    public List<UserCourseProgressDTO> getProgressCoursesByUserId(Long userId) {
        return enrollmentRepository.findProgressDTOExcludingCompleted(userId);
    }

    @Override
    public int updateStatusToCompleted(Long userId, Long courseId) {
        return enrollmentRepository.updateStatusCompleted(userId, courseId);
    }

}
