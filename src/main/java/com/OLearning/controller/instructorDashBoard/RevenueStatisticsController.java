package com.OLearning.controller.instructorDashboard;

import com.OLearning.service.instructor.RevenueStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/instructor/revenue")
public class RevenueStatisticsController {

    @Autowired
    private RevenueStatisticsService revenueStatisticsService;

    // API: /instructor/revenue/statistics?range=day|week|month|year
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam String range
    ) {
        // TODO: Lấy instructorId từ session nếu cần
        Map<String, Object> result = revenueStatisticsService.getRevenueStatistics(range, null);
        return ResponseEntity.ok(result);
    }

    // API: /instructor/revenue/course-sales
    @GetMapping("/course-sales")
    public ResponseEntity<List<Map<String, Object>>> getCourseSales() {
        // TODO: Lấy instructorId từ session nếu cần
        List<Map<String, Object>> result = revenueStatisticsService.getCourseSales(null);
        return ResponseEntity.ok(result);
    }
} 