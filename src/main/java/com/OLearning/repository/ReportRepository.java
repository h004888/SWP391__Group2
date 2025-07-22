package com.OLearning.repository;

import com.OLearning.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // Tìm tất cả báo cáo theo loại
//    List<Report> findByReportType(String reportType);
//
//    // Tìm tất cả báo cáo theo trạng thái
//    List<Report> findByStatus(String status);
//
//    // Tìm tất cả báo cáo của một khóa học
//    List<Report> findByCourse_CourseId(Long courseId);
//
//    // Tìm tất cả báo cáo của một người dùng
//    List<Report> findByUser_UserId(Long userId);
//
//    // Tìm báo cáo theo loại và trạng thái
//    List<Report> findByReportTypeAndStatus(String reportType, String status);

    Page<Report> findByReportTypeAndStatus(String reportType, String status, Pageable pageable);
    Page<Report> findByReportType(String reportType, Pageable pageable);
    Page<Report> findByStatus(String status, Pageable pageable);
    Report findFirstByCourse_CourseIdAndNotification_NotificationId(Long courseId, Long notificationId);
} 