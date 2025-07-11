package com.OLearning.service.courseMaintance.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fee;
import com.OLearning.entity.Notification;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.FeesRepository;
import com.OLearning.repository.NotificationRepository;
import com.OLearning.repository.CourseMaintenanceRepository;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.email.EmailService;
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
import java.util.stream.Collectors;

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

    @Autowired
    private EmailService emailService;

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
    
    @Override
    public List<Fee> getListFees() {
        return feesRepository.findAll();
    }

    @Override
    public void updateFee(Long feeId, Long minEnrollments, Long maxEnrollments, Long maintenanceFee) {
        Fee fee = feesRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));
        
        fee.setMinEnrollments(minEnrollments);
        fee.setMaxEnrollments(maxEnrollments);
        fee.setMaintenanceFee(maintenanceFee);
        
        feesRepository.save(fee);
    }

    @Override
    public void deleteFee(Long feeId) {
        Fee fee = feesRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Fee not found"));
        
        // Check if fee is being used in any course maintenance
        if (!fee.getCourseMaintenances().isEmpty()) {
            throw new RuntimeException("Cannot delete fee that is being used in course maintenances");
        }
        
        feesRepository.delete(fee);
    }

    @Override
    public void addFee(Long minEnrollments, Long maxEnrollments, Long maintenanceFee) {
        // Validate input
        if (minEnrollments >= maxEnrollments) {
            throw new RuntimeException("Min enrollments must be less than max enrollments");
        }
        
        // Check for overlapping ranges
        List<Fee> existingFees = feesRepository.findAll();
        for (Fee existingFee : existingFees) {
            if ((minEnrollments <= existingFee.getMaxEnrollments() && 
                 maxEnrollments >= existingFee.getMinEnrollments())) {
                throw new RuntimeException("Fee range overlaps with existing fee range");
            }
        }
        
        Fee newFee = new Fee();
        newFee.setMinEnrollments(minEnrollments);
        newFee.setMaxEnrollments(maxEnrollments);
        newFee.setMaintenanceFee(maintenanceFee);
        
        feesRepository.save(newFee);
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

            // Gửi email thông báo
            try {
                emailService.sendMaintenanceInvoiceEmail(
                        course.getInstructor().getEmail(),
                        course.getInstructor().getFullName(),
                        course.getTitle(),
                        monthYear.format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        enrollmentCount,
                        fee.getMaintenanceFee().doubleValue(),
                        dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );

                // Save notification
                Notification notification = new Notification();
                notification.setUser(course.getInstructor());
                notification.setMessage(String.format(
                        "Thông báo phí bảo trì khóa học cho khóa học '%s'. Phí bảo trì: %,.0f VND. Hạn thanh toán: %s",
                        course.getTitle(),
                        fee.getMaintenanceFee().doubleValue(),
                        dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ));
                notification.setType("MAINTENANCE_INVOICE");
                notification.setSentAt(LocalDateTime.now());
                notification.setCourse(course);
                notification.setUser(course.getInstructor());
                notification.setStatus(false);
                notificationRepository.save(notification);
            } catch (Exception e) {
                System.err.println("Error sending maintenance invoice email: " + e.getMessage());
            }
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

    @Scheduled(cron = "0 0 9 * * ?") // Chạy mỗi ngày lúc 9:00 sáng
    @Override
    public void checkOverdueMaintenance() {
        LocalDate today = LocalDate.now();
        List<CourseMaintenance> overdueMaintenances = courseMaintenanceRepository.findAll().stream()
                .filter(m -> m.getDueDate().isBefore(today))
                .collect(Collectors.toList());

        for (CourseMaintenance maintenance : overdueMaintenances) {
            Course course = maintenance.getCourse();
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(maintenance.getDueDate(), today);
            
            try {
                if (daysOverdue >= 14) {
                    // Khóa tài khoản giảng viên
                    course.getInstructor().setStatus(false);
                    courseRepository.save(course);
                    
                    // Gửi email thông báo
                    emailService.sendOverdueMaintenanceEmail(
                        course.getInstructor().getEmail(),
                        course.getInstructor().getFullName(),
                        course.getTitle(),
                        maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        maintenance.getEnrollmentCount(),
                        maintenance.getFee().getMaintenanceFee().doubleValue(),
                        maintenance.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );

                    // Lưu thông báo
                    Notification notification = new Notification();
                    notification.setUser(course.getInstructor());
                    notification.setMessage(String.format(
                        "Tài khoản của bạn đã bị khóa do không thanh toán phí bảo trì khóa học '%s' sau 14 ngày quá hạn",
                        course.getTitle()
                    ));
                    notification.setType("ACCOUNT_LOCKED");
                    notification.setSentAt(LocalDateTime.now());
                    notification.setCourse(course);
                    notification.setStatus(false);
                    notificationRepository.save(notification);

                } else if (daysOverdue >= 7) {
                    // Tạm ẩn khóa học
                    course.setStatus("hidden");
                    courseRepository.save(course);
                    
                    // Gửi email thông báo
                    emailService.sendOverdueMaintenanceEmail(
                        course.getInstructor().getEmail(),
                        course.getInstructor().getFullName(),
                        course.getTitle(),
                        maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        maintenance.getEnrollmentCount(),
                        maintenance.getFee().getMaintenanceFee().doubleValue(),
                        maintenance.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );

                    // Lưu thông báo
                    Notification notification = new Notification();
                    notification.setUser(course.getInstructor());
                    notification.setMessage(String.format(
                        "Khóa học '%s' đã bị tạm ẩn do không thanh toán phí bảo trì sau 7 ngày quá hạn",
                        course.getTitle()
                    ));
                    notification.setType("COURSE_HIDDEN");
                    notification.setSentAt(LocalDateTime.now());
                    notification.setCourse(course);
                    notification.setStatus(false);
                    notificationRepository.save(notification);

                } else if (daysOverdue >= 3) {
                    // Gửi nhắc nhở lần 2
                    emailService.sendSecondReminderEmail(
                        course.getInstructor().getEmail(),
                        course.getInstructor().getFullName(),
                        course.getTitle(),
                        maintenance.getMonthYear().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                        maintenance.getEnrollmentCount(),
                        maintenance.getFee().getMaintenanceFee().doubleValue(),
                        maintenance.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    );

                    // Lưu thông báo
                    Notification notification = new Notification();
                    notification.setUser(course.getInstructor());
                    notification.setMessage(String.format(
                        "Nhắc nhở lần 2: Phí bảo trì khóa học '%s' đã quá hạn 3 ngày",
                        course.getTitle()
                    ));
                    notification.setType("SECOND_REMINDER");
                    notification.setSentAt(LocalDateTime.now());
                    notification.setCourse(course);
                    notification.setStatus(false);
                    notificationRepository.save(notification);
                }

                // Cập nhật trạng thái maintenance
                maintenance.setStatus("overdue");
                courseMaintenanceRepository.save(maintenance);

            } catch (Exception e) {
                System.err.println("Error processing overdue maintenance: " + e.getMessage());
            }
        }
    }
}
