package com.OLearning.service.chart;

import com.OLearning.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.OLearning.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;

@Service
public class RevenueStatisticsService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Map<String, Object> getRevenueChart(String range, Long instructorId) {
        List<Map<String, Object>> data = new ArrayList<>();
        List<Object[]> results;
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        Locale locale = Locale.ENGLISH;
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("MMM dd", locale);
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM", locale);
        switch (range) {
            case "day": {
                // 7 ngày của tuần hiện tại (Monday-Sunday)
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                List<String> labels = new ArrayList<>();
                Map<String, Double> revenueMap = new LinkedHashMap<>();
                Map<String, Long> ordersMap = new LinkedHashMap<>();
                for (int i = 0; i < 7; i++) {
                    LocalDate d = monday.plusDays(i);
                    String label = d.format(dayFmt);
                    labels.add(label);
                    revenueMap.put(label, 0.0);
                    ordersMap.put(label, 0L);
                }
                LocalDateTime start = monday.atStartOfDay();
                LocalDateTime end = monday.plusDays(6).atTime(LocalTime.MAX);
                results = orderDetailRepository.findRevenueAndOrdersByDay(instructorId, start, end);
                for (Object[] row : results) {
                    int year = (int) row[0];
                    int month = (int) row[1];
                    int day = (int) row[2];
                    double revenue = ((Number) row[3]).doubleValue();
                    long orders = ((Number) row[4]).longValue();
                    LocalDate d = LocalDate.of(year, month, day);
                    String label = d.format(dayFmt);
                    if (revenueMap.containsKey(label)) {
                        revenueMap.put(label, revenue);
                        ordersMap.put(label, orders);
                    }
                }
                for (String label : labels) {
                    data.add(Map.of("label", label, "revenue", revenueMap.get(label), "orders", ordersMap.get(label)));
                }
                break;
            }
            case "week": {
                // BỎ QUA KHÔNG XỬ LÝ WEEK
                break;
            }
            case "month": {
                // 12 tháng của năm hiện tại
                int year = today.getYear();
                List<String> labels = new ArrayList<>();
                Map<String, Double> revenueMap = new LinkedHashMap<>();
                Map<String, Long> ordersMap = new LinkedHashMap<>();
                for (int m = 1; m <= 12; m++) {
                    LocalDate d = LocalDate.of(year, m, 1);
                    String label = d.format(monthFmt);
                    labels.add(label);
                    revenueMap.put(label, 0.0);
                    ordersMap.put(label, 0L);
                }
                LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
                LocalDateTime end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
                results = orderDetailRepository.findRevenueAndOrdersByMonth(instructorId, start, end);
                for (Object[] row : results) {
                    int y = (int) row[0];
                    int m = (int) row[1];
                    double revenue = ((Number) row[2]).doubleValue();
                    long orders = ((Number) row[3]).longValue();
                    if (y == year) {
                        LocalDate d = LocalDate.of(y, m, 1);
                        String label = d.format(monthFmt);
                        if (revenueMap.containsKey(label)) {
                            revenueMap.put(label, revenue);
                            ordersMap.put(label, orders);
                        }
                    }
                }
                for (String label : labels) {
                    data.add(Map.of("label", label, "revenue", revenueMap.get(label), "orders", ordersMap.get(label)));
                }
                break;
            }
            case "quarter": {
                // 4 quý của năm hiện tại
                int year = today.getYear();
                List<String> labels = Arrays.asList("Q1", "Q2", "Q3", "Q4");
                Map<String, Double> revenueMap = new LinkedHashMap<>();
                Map<String, Long> ordersMap = new LinkedHashMap<>();
                for (String label : labels) {
                    revenueMap.put(label, 0.0);
                    ordersMap.put(label, 0L);
                }
                LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
                LocalDateTime end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
                results = orderDetailRepository.findRevenueAndOrdersByQuarter(instructorId, start, end);
                for (Object[] row : results) {
                    int y = (int) row[0];
                    int q = (int) row[1];
                    double revenue = ((Number) row[2]).doubleValue();
                    long orders = ((Number) row[3]).longValue();
                    if (y == year) {
                        String label = "Q" + q;
                        if (revenueMap.containsKey(label)) {
                            revenueMap.put(label, revenue);
                            ordersMap.put(label, orders);
                        }
                    }
                }
                for (String label : labels) {
                    data.add(Map.of("label", label, "revenue", revenueMap.get(label), "orders", ordersMap.get(label)));
                }
                break;
            }
        }
        return Map.of("data", data);
    }
}