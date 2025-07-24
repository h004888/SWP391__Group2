package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.order.InvoiceDTO;
import com.OLearning.entity.OrderDetail;
import com.OLearning.repository.CourseMaintenanceRepository;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/instructor/invoice")
public class InvoiceController {

    private final OrdersService ordersService;
    private final CourseMaintenanceService courseMaintenanceService;
    private final OrdersRepository ordersRepository;

    @GetMapping
    public String getInvoicePage(Model model, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        // Get initial data for first page
        Page<InvoiceDTO> invoicePage = ordersService.findInvoiceByInstructorId(userId, pageNo);
        Long maintain = courseMaintenanceService.sumCourseMaintainForInstructor(userId);
        if (maintain == null) {
            maintain = 0L;
        }
        Double receive = ordersService.sumRevenueByInstructorId(userId) - maintain;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formatted = formatter.format(receive) + " VND";
        model.addAttribute("orders", invoicePage.getContent());
        model.addAttribute("currentPage", invoicePage.getNumber() + 1);
        model.addAttribute("totalPage", invoicePage.getTotalPages());
        model.addAttribute("totalElement", invoicePage.getTotalElements());
        model.addAttribute("sumRevenue", ordersService.sumRevenueByInstructorId(userId));
        model.addAttribute("currentMonth", ordersRepository.getRevenueStatsByInstructor(userId).getCurrentRevenue());
        model.addAttribute("growth", ordersRepository.getRevenueStatsByInstructor(userId).getGrowth());
        model.addAttribute("maintain", maintain);
        model.addAttribute("formatted", formatted);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/invoice :: invoice");
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = userDetails.getUserId();

        List<OrderDetail> orderDetails = ordersService.getInstructorOrderDetailsByOrderId(orderId, instructorId);

        // Check if instructor has any courses in this order
        if (orderDetails.isEmpty()) {
            return "redirect:/instructor/invoice?error=access_denied";
        }
        // TÍNH TỔNG DOANH THU CHO INSTRUCTOR
        double totalAmount = orderDetails.stream()
                .mapToDouble(detail -> {
                    double unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : 0;
                    double discount = detail.getCourse().getDiscount() != null ? detail.getCourse().getDiscount() : 0;
                    return unitPrice * (1 - discount / 100.0);
                }).sum();
        model.addAttribute("accNamePage", "Order Details");
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("orderId", orderId);
        model.addAttribute("totalAmount", totalAmount); // <-- TRUYỀN VÀO MODEL
        model.addAttribute("fragmentContent", "instructorDashBoard/fragments/orderDetailsContent :: contentOrderDetails");
        return "instructorDashboard/indexUpdate";
    }
}
