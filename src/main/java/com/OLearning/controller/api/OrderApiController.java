package com.OLearning.controller.api;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class OrderApiController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @GetMapping("/api/order/status")
    @ResponseBody
    public Map<String, String> getOrderStatus(@RequestParam Long orderId) {
        String status = ordersService.getOrderStatusById(orderId);
        return Map.of("status", status);
    }

    @GetMapping("/api/maintenance/status")
    @ResponseBody
    public Map<String, String> getMaintenanceStatus(@RequestParam Long maintenanceId) {
        String status = courseMaintenanceService.getMaintenanceStatusById(maintenanceId);
        return Map.of("status", status);
    }
} 