package com.OLearning.repository;

import com.OLearning.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReportTypeAndStatus(String reportType, String status, Pageable pageable);

    Page<Report> findByReportType(String reportType, Pageable pageable);

    Page<Report> findByStatus(String status, Pageable pageable);

    List<Report> findByCourse_CourseId(Long courseId);
}