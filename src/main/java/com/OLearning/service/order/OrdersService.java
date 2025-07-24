package com.OLearning.service.order;

import com.OLearning.dto.course.CourseSalesDTO;
import com.OLearning.dto.order.*;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrdersService {
    // Existing methods
//    Order createOrder(OrdersDTO orderDTO);
//    Order findOrderById(Long orderId);
//
//    // New methods for invoice
//    Page<InvoiceDTO> getInvoicesByInstructor(String instructorEmail, InvoiceFilterDTO filter);
//    Double calculateTotalRevenue(String instructorEmail);
//    Double calculateCurrentMonthRevenue(String instructorEmail);
//    Double calculateRevenueGrowth(String instructorEmail);
//    Long calculatePlatformFees(String instructorEmail);
//    Double calculateNetRevenue(String instructorEmail);
    List<CourseSalesDTO> getCourseSalesForInstructor(Long instructorId, String startDate, String endDate);
    Map<String, Double> getMonthlyRevenueForInstructor(Long instructorId, String startDate, String endDate);
    List<OrderStatsDTO> getStatsForInstructor(Long instructorId);
    Page<InvoiceDTO> findInvoiceByInstructorId(Long instructorId, int pageNo);
    Double sumRevenueByInstructorId(Long instructorId);
    RevenueDTO revenueMonth(Long instructorId);
    Page<InvoiceDTO> findOrderDateAsc(Long instructorId, int pageNo);
    Page<InvoiceDTO> findOrderAmountAsc(Long instructorId, int pageNo);
    Page<InvoiceDTO> findOrderAmountDesc(Long instructorId, int pageNo);
    List<OrderDetail> getInstructorOrderDetailsByOrderId(Long orderId, Long instructorId);
}
