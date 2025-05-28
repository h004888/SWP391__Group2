package com.OLearning.controller.adminDashboard;

import com.OLearning.dto.adminDashBoard.OrdersDTO;
import com.OLearning.service.adminDashBoard.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    @GetMapping
    public String getAllOrders(Model model) {
        List<OrdersDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "adminDashboard/orders";
    }

    @GetMapping("/searchByUsername")
    public String searchByUsername(@RequestParam(value = "username", required = false) String username, Model model) {
        List<OrdersDTO> orders = orderService.findByUsername(username);
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        return "adminDashboard/orders";
    }

    @GetMapping("/searchByCourseName")
    public String searchByCourseName(@RequestParam(value = "courseName", required = false) String courseName, Model model) {
        List<OrdersDTO> orders = orderService.findByCourseName(courseName);
        model.addAttribute("orders", orders);
        model.addAttribute("courseName", courseName);
        return "adminDashboard/orders";
    }

    @GetMapping("/sortByAmount")
    public String sortByAmount(@RequestParam(value = "direction", required = false) String direction, Model model) {
        List<OrdersDTO> orders = orderService.sortByAmount(direction);
        model.addAttribute("orders", orders);
        model.addAttribute("amountDirection", direction);
        return "adminDashboard/orders";
    }

    @GetMapping("/sortByDate")
    public String sortByDate(@RequestParam(value = "direction", required = false) String direction, Model model) {
        List<OrdersDTO> orders = orderService.sortByDate(direction);
        model.addAttribute("orders", orders);
        model.addAttribute("dateDirection", direction);
        return "adminDashboard/orders";
    }
}