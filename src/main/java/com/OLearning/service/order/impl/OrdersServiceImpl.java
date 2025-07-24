package com.OLearning.service.order.impl;

import com.OLearning.dto.order.*;
import com.OLearning.entity.OrderDetail;
import com.OLearning.repository.OrderDetailRepository;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.service.order.OrdersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.OLearning.dto.course.CourseSalesDTO;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.OLearning.dto.order.InvoiceDTO;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;




    @Override
    public List<CourseSalesDTO> getCourseSalesForInstructor(Long instructorId, String startDate, String endDate) {
        // Không filter theo ngày ở đây, chỉ lấy tất cả các khóa đã bán của instructor
        List<Object[]> raw = orderDetailRepository.findCourseSalesAndRevenueByInstructor(instructorId);
        List<CourseSalesDTO> result = new java.util.ArrayList<>();
        for (Object[] row : raw) {
            String courseName = (String) row[0];
            int sold = ((Number) row[1]).intValue();
            double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            result.add(new CourseSalesDTO(courseName, sold, revenue));
        }
        return result;
    }


    @Override
    public Map<String, Double> getMonthlyRevenueForInstructor(Long instructorId, String startDate, String endDate) {
        // Nếu không truyền ngày, lấy mặc định 1 năm gần nhất
        java.time.LocalDateTime start;
        java.time.LocalDateTime end;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            start = java.time.LocalDate.parse(startDate, fmt).atStartOfDay();
            end = java.time.LocalDate.parse(endDate, fmt).atTime(23,59,59);
        } else {
            end = java.time.LocalDateTime.now();
            start = end.minusYears(1).withDayOfMonth(1).withMonth(1).withHour(0).withMinute(0).withSecond(0);
        }
        List<Object[]> raw = orderDetailRepository.findRevenueAndOrdersByMonth(instructorId, start, end);
        Map<String, Double> result = new java.util.LinkedHashMap<>();
        for (Object[] row : raw) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            String key = String.format("%d-%02d", year, month);
            result.put(key, revenue);
        }
        return result;
    }

    @Override
    public List<OrderStatsDTO> getStatsForInstructor(Long instructorId) {
        List<Object[]> rawStats = ordersRepository.getOrderStatsByInstructor(instructorId);
        List<OrderStatsDTO> stats = new ArrayList<>();
        for (Object[] row : rawStats) {
            OrderStatsDTO dto = new OrderStatsDTO();
            dto.setMonth((Integer) row[0]);
            dto.setOrderCount(((Number) row[1]).intValue());
            dto.setTotalAmount(((Number) row[2]).doubleValue());
            stats.add(dto);
        }
        return stats;
    }

    @Override
    public Page<InvoiceDTO> findInvoiceByInstructorId(Long instructorId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return ordersRepository.findInvoiceByInstructorId(instructorId, pageable);
    }

    @Override
    public Double sumRevenueByInstructorId(Long instructorId) {
        return ordersRepository.sumRevenueOrders(instructorId);
    }

    @Override
    public RevenueDTO revenueMonth(Long instructorId) {
        return ordersRepository.getRevenueStatsByInstructor(instructorId);
    }

    @Override
    public Page<InvoiceDTO> findOrderDateAsc(Long instructorId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return ordersRepository.findInvoiceByInstructorIdAscDate(instructorId, pageable);
    }

    @Override
    public Page<InvoiceDTO> findOrderAmountAsc(Long instructorId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return ordersRepository.findInvoiceByInstructorIdAmountAsc(instructorId, pageable);
    }

    @Override
    public Page<InvoiceDTO> findOrderAmountDesc(Long instructorId, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return ordersRepository.findInvoiceByInstructorIdAmountDesc(instructorId, pageable);
    }

    @Override
    public List<OrderDetail> getInstructorOrderDetailsByOrderId(Long orderId, Long instructorId) {
        List<OrderDetail> allOrderDetails = orderDetailRepository.findByOrderOrderId(orderId);

        // Filter only order details for instructor's courses
        return allOrderDetails.stream()
                .filter(detail -> detail.getCourse() != null &&
                        detail.getCourse().getInstructor() != null &&
                        detail.getCourse().getInstructor().getUserId().equals(instructorId))
                .collect(Collectors.toList());
    }
}
