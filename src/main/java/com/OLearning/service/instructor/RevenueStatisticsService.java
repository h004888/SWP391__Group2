package com.OLearning.service.instructor;

import com.OLearning.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.OLearning.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class RevenueStatisticsService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    private Long getCurrentInstructorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }

    public Map<String, Object> getRevenueStatistics(String range, Long instructorId) {
        if (instructorId == null) instructorId = getCurrentInstructorId();
        if (instructorId == null) return Map.of("labels", List.of(), "data", List.of());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start, end;
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        if ("day".equals(range)) {
            start = now.minusDays(6).with(LocalTime.MIN);
            end = now.with(LocalTime.MAX);
            List<Object[]> results = orderDetailRepository.findRevenueByDay(instructorId, start, end);
            for (Object[] row : results) {
                int year = (int) row[0];
                int month = (int) row[1];
                int day = (int) row[2];
                double revenue = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
                String label = String.format("%d-%02d-%02d", year, month, day);
                labels.add(label);
                data.add(revenue);
            }
        } else if ("month".equals(range)) {
            start = now.minusMonths(5).withDayOfMonth(1).with(LocalTime.MIN);
            end = now.with(LocalTime.MAX);
            List<Object[]> results = orderDetailRepository.findRevenueByMonth(instructorId, start, end);
            for (Object[] row : results) {
                int year = (int) row[0];
                int month = (int) row[1];
                double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
                String label = String.format("%d-%02d", year, month);
                labels.add(label);
                data.add(revenue);
            }
        } else if ("year".equals(range)) {
            start = now.minusYears(3).withDayOfYear(1).with(LocalTime.MIN);
            end = now.with(LocalTime.MAX);
            List<Object[]> results = orderDetailRepository.findRevenueByYear(instructorId, start, end);
            for (Object[] row : results) {
                int year = (int) row[0];
                double revenue = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                String label = String.valueOf(year);
                labels.add(label);
                data.add(revenue);
            }
        } else {
            // Tuần chưa hỗ trợ
            return Map.of("labels", List.of(), "data", List.of());
        }
        return Map.of("labels", labels, "data", data);
    }

    public List<Map<String, Object>> getCourseSales(Long instructorId) {
        if (instructorId == null) instructorId = getCurrentInstructorId();
        if (instructorId == null) return List.of();
        List<Object[]> results = orderDetailRepository.findCourseSalesAndRevenueByInstructor(instructorId);
        List<Map<String, Object>> courses = new ArrayList<>();
        for (Object[] row : results) {
            String title = (String) row[0];
            Long sold = ((Number) row[1]).longValue();
            Double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            courses.add(Map.of("title", title, "sold", sold, "revenue", revenue));
        }
        return courses;
    }
} 