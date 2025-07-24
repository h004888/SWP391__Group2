package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InvoiceDTO;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instructor/invoice")
public class InstructorRestController {
    @Autowired
    private OrdersService ordersService;
        @GetMapping("/ajax")
        private ResponseEntity<Page<InvoiceDTO>> allPage(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            Page<InvoiceDTO> page = ordersService.findInvoiceByInstructorId(userId, pageNo);
            return ResponseEntity.ok(page);
        }
}
