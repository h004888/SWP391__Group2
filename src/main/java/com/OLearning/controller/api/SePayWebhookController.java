package com.OLearning.controller.api;

import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SePayWebhookController {

    @Autowired
    private OrdersService ordersService;

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
            Long orderId = extractOrderId(rawContent);
            if (orderId != null) {
                ordersService.markOrderAsPaid(orderId);
                return "success";
            } else {
                return "error: invalid order ID in content";
            }
        } catch (NumberFormatException e) {
            return "error: invalid order ID format: " + e.getMessage();
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
                    // Dừng lại khi gặp ký tự không phải số
                    break;
                }
            }
            if (orderIdBuilder.length() > 0) {
                return Long.parseLong(orderIdBuilder.toString());
            }
        }
        return null;
    }
} 