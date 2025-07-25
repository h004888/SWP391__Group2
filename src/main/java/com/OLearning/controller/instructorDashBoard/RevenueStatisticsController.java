package com.OLearning.controller.instructorDashBoard;

import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.chart.RevenueStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/instructor/revenue")
public class RevenueStatisticsController {

    @Autowired
    private RevenueStatisticsService revenueStatisticsService;
    @GetMapping("/chart")
    public ResponseEntity<Map<String, Object>> getRevenueChart(
            @RequestParam String range // "day", "week", "month", "quarter"
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();
        // Lấy instructorId từ session nếu cần
        Map<String, Object> result = revenueStatisticsService.getRevenueChart(range, instructorId);
        return ResponseEntity.ok(result);
    }
} 