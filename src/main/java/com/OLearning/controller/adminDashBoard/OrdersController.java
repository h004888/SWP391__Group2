package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.OrderDetail;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

    private static final String ACC_NAME_PAGE_MANAGEMENT = "Invoice";
    private static final String ACC_NAME_PAGE_ORDER_DETAILS = "Order Details";

    @Autowired
    private OrdersService ordersService;

    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "amountDirection", required = false) String amountDirection,
                               @RequestParam(value = "orderType", required = false) String orderType,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate) {
        // Default to PAID status for initial load
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrdersWithStatus(
            username, amountDirection, orderType, startDate, endDate, "PAID", page, size);
        model.addAttribute("accNamePage", ACC_NAME_PAGE_MANAGEMENT);
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("username", username);
        model.addAttribute("amountDirection", amountDirection);
        model.addAttribute("orderType", orderType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        return "adminDashBoard/index";
    }

    //Filter with ajax for tab functionality
    @GetMapping("/filter")
    public String filterOrders(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrdersWithStatus(
            username, amountDirection, orderType, startDate, endDate, status, page, size);
        model.addAttribute("orders", ordersPage.getContent());

        return "adminDashBoard/fragments/ordersTableRowContent :: ordersTableRowContent";
    }

    // New endpoint for pagination only
    @GetMapping("/pagination")
    public String getPagination(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrdersWithStatus(
            username, amountDirection, orderType, startDate, endDate, status, page, size);
        
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "adminDashBoard/fragments/ordersTableRowContent :: ordersPagination";
    }

    @GetMapping("/count")
    @ResponseBody
    public long getOrderCount(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "orderType", required = false) String orderType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "status", required = false) String status) {
        
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrdersWithStatus(
            username, amountDirection, orderType, startDate, endDate, status, 0, 1); // Get only 1 item to check total

        return ordersPage.getTotalElements();
    }

    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("accNamePage", ACC_NAME_PAGE_ORDER_DETAILS);
        List<OrderDetail> orderDetails = ordersService.getOrderDetailsByOrderId(orderId);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("orderId", orderId);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/orderDetailsContent :: contentOrderDetails");
        return "adminDashBoard/index";
    }
}