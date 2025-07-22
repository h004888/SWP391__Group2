package com.OLearning.controller.api;

import com.OLearning.service.order.OrdersService;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import com.OLearning.entity.CourseMaintenance;

@RestController
public class SePayWebhookController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @PostMapping("/api/payment/sepay/webhook")
    public String handleWebhook(@RequestBody Map<String, Object> payload) {
        // Sử dụng trường 'content' hoặc 'description' từ payload của SePay
        String rawContent = (String) payload.get("content");
        if (rawContent == null) {
            rawContent = (String) payload.get("description");
        }
        if (rawContent == null) {
            return "error: missing content or description";
        }
        try {
            // Nếu là ORDER
            if (rawContent.contains("ORDER")) {
                Long orderId = extractOrderId(rawContent);
                if (orderId != null) {
                    String refCode = payload.get("referenceCode") != null ? payload.get("referenceCode").toString()
                            : payload.get("id") != null ? payload.get("id").toString() : null;
                    ordersService.markOrderAsPaid(orderId, refCode);
                    return "success";
                }
            }
            // Nếu là MAINTENANCE
            if (rawContent.contains("MAINTENANCE")) {
                Long maintenanceId = extractMaintenanceId(rawContent);
                if (maintenanceId != null) {
                    String refCode = payload.get("referenceCode") != null ? payload.get("referenceCode").toString()
                            : payload.get("id") != null ? payload.get("id").toString() : null;
                    boolean success = markCourseMaintenanceAsPaid(maintenanceId, refCode);
                    return success ? "success" : "error: could not mark maintenance as paid";
                }
            }
            return "error: invalid order ID or maintenance ID in content";
        } catch (NumberFormatException e) {
            return "error: invalid ID format: " + e.getMessage();
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    private Long extractOrderId(String rawContent) {
        // Tìm kiếm chuỗi "ORDER" và trích xuất số sau đó
        String orderPrefix = "ORDER";
        int startIndex = rawContent.indexOf(orderPrefix);
        if (startIndex != -1) {
            // Bắt đầu từ sau chuỗi "ORDER"
            int numStartIndex = startIndex + orderPrefix.length();
            StringBuilder orderIdBuilder = new StringBuilder();
            for (int i = numStartIndex; i < rawContent.length(); i++) {
                char c = rawContent.charAt(i);
                if (Character.isDigit(c)) {
                    orderIdBuilder.append(c);
                } else {
                    break;
                }
            }
            if (orderIdBuilder.length() > 0) {
                return Long.parseLong(orderIdBuilder.toString());
            }
        }
        return null;
    }

    private Long extractMaintenanceId(String rawContent) {
        String prefix = "MAINTENANCE";
        int startIndex = rawContent.indexOf(prefix);
        if (startIndex != -1) {
            int numStartIndex = startIndex + prefix.length();
            StringBuilder idBuilder = new StringBuilder();
            for (int i = numStartIndex; i < rawContent.length(); i++) {
                char c = rawContent.charAt(i);
                if (Character.isDigit(c)) {
                    idBuilder.append(c);
                } else {
                    break;
                }
            }
            if (idBuilder.length() > 0) {
                return Long.parseLong(idBuilder.toString());
            }
        }
        return null;
    }

    private boolean markCourseMaintenanceAsPaid(Long maintenanceId, String refCode) {
        return courseMaintenanceService.processMaintenancePayment(maintenanceId, refCode);
    }
} 