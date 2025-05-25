package com.OLearning.controller.adminDashboard;

import com.OLearning.dto.adminDashBoard.PaymentDTO;
import com.OLearning.service.adminDashBoard.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("payments")
    public String getAllPayments(Model model) {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "adminDashboard/payment";
    }

    @GetMapping("payments/searchByUsername")
    public String searchByUsername(@RequestParam(value = "username", required = false) String username, Model model) {
        List<PaymentDTO> payments = paymentService.findByUsername(username);
        model.addAttribute("payments", payments);
        model.addAttribute("username", username);
        return "adminDashboard/payment";
    }

    @GetMapping("payments/searchByCourseName")
    public String searchByCourseName(@RequestParam(value = "courseName", required = false) String courseName, Model model) {
        List<PaymentDTO> payments = paymentService.findByCourseName(courseName);
        model.addAttribute("payments", payments);
        model.addAttribute("courseName", courseName);
        return "adminDashboard/payment";
    }

    @GetMapping("payments/sortByAmount")
    public String sortByAmount(@RequestParam(value = "direction", required = false) String direction, Model model) {
        List<PaymentDTO> payments = paymentService.sortByAmount(direction);
        model.addAttribute("payments", payments);
        model.addAttribute("amountDirection", direction);
        return "adminDashboard/payment";
    }

    @GetMapping("payments/sortByDate")
    public String sortByDate(@RequestParam(value = "direction", required = false) String direction, Model model) {
        List<PaymentDTO> payments = paymentService.sortByDate(direction);
        model.addAttribute("payments", payments);
        model.addAttribute("dateDirection", direction);
        return "adminDashboard/payment";
    }
}