package com.OLearning.service.courseMaintance.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fees;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.FeesRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.CourseMaintenanceRepository;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

@Service
public class CourseMaintenanceServiceImpl implements CourseMaintenanceService {
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
    public Page<CourseMaintenance> getAllCourseMaintenances(Pageable pageable) {
        return courseMaintenanceRepository.findAll(pageable);
    }

    @Override
    public List<CourseMaintenance> searchByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            return courseMaintenanceRepository.findByCourseInstructorUsernameContaining(username);
        }
        return courseMaintenanceRepository.findAll();
    }

    @Override
    public Page<CourseMaintenance> filterMaintenances(String username, String status, LocalDate monthYear, Pageable pageable) {
        return courseMaintenanceRepository.findByUsernameAndStatusAndMonthYear(username, status, monthYear, pageable);
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

            Fees fee = feesRepository.findByMinEnrollmentsLessThanEqualAndMaxEnrollmentsGreaterThanEqual(enrollmentCount, enrollmentCount);
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

    @Override
    public Map<String, Object> getMaintenanceRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> maintenanceData = new TreeMap<>();
        List<CourseMaintenance> maintenances = courseMaintenanceRepository.findByMonthYearBetween(startDate, endDate);
        
        for (CourseMaintenance maintenance : maintenances) {
            String monthYear = maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy"));
            Double fee = maintenance.getFee().getMaintenanceFee().doubleValue();
            Long enrollmentCount = maintenance.getEnrollmentCount();
            
            Map<String, Object> monthData = (Map<String, Object>) maintenanceData.computeIfAbsent(monthYear, k -> new HashMap<>());
            monthData.put("revenue", fee);
            monthData.put("enrollmentCount", enrollmentCount);
        }
        
        return maintenanceData;
    }
}
