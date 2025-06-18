package com.OLearning.service.courseMaintenance.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fee;
import com.OLearning.repository.*;
import com.OLearning.service.courseMaintenance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class CourseMaintenanceServiceImpl implements CourseMaintenanceService{
    @Autowired
    private CourseMaintenanceRepository courseMaintenanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<CourseMaintenance> getAllCourseMaintenances() {
        return courseMaintenanceRepository.findAll();
    }

    @Override
    public List<CourseMaintenance> searchByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            return courseMaintenanceRepository.findByCourseInstructorUsernameContaining(username);
        }
        return courseMaintenanceRepository.findAll();
    }



    @Scheduled(cron = "0 59 23 L * ?") // Chạy vào 23:59 ngày cuối tháng
    @Override
    public void processMonthlyMaintenance() {
        LocalDate currentDate = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate monthYear = yearMonth.atEndOfMonth();
        LocalDate dueDate = monthYear.plusDays(7);

        List<Course> courses = courseRepository.findAll();

        for (Course course : courses) {
            // Kiểm tra nếu đã tồn tại thì bỏ qua
            if (courseMaintenanceRepository.existsByCourseCourseIdAndDueDate(course.getCourseId(), dueDate)) {
                continue;
            }

            Long enrollmentCount = enrollmentRepository.countByCourseIdAndMonth(
                    course.getCourseId(), yearMonth.getYear(), yearMonth.getMonthValue());

            Fee fee = feesRepository.findByMinEnrollmentsLessThanEqualAndMaxEnrollmentsGreaterThanEqual(enrollmentCount, enrollmentCount);
            if (fee == null) continue;

            CourseMaintenance maintenance = new CourseMaintenance();
            maintenance.setCourse(course);
            maintenance.setFee(fee);
            maintenance.setMonthYear(monthYear);
            maintenance.setEnrollmentCount(enrollmentCount);
            maintenance.setStatus("pending");
            maintenance.setDueDate(dueDate);
            maintenance.setSentAt(LocalDateTime.now());

            courseMaintenanceRepository.save(maintenance);
        }
    }

//    @Scheduled(cron = "0 * * * * ?") //  Chạy mỗi phút
//    @Override
//    public void updateMaintenanceStatus() {
//        LocalDate today = LocalDate.now();
//        List<CourseMaintenance> maintenances = courseMaintenanceRepository.findAll();
//
//        for (CourseMaintenance maintenance : maintenances) {
//            if (maintenance.getOrders() != null && "completed".equals(maintenance.getOrders().getStatus())) {
//                maintenance.setStatus("completed");
//            } else if (maintenance.getDueDate().isBefore(today) && !"completed".equals(maintenance.getStatus())) {
//                maintenance.setStatus("overdue");
//            }
//            courseMaintenanceRepository.save(maintenance);
//        }
//    }
}
