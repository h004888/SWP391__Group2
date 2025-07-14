package com.OLearning.controller.homePage;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.coinTransaction.CoinTransactionService;
import com.OLearning.service.payment.VNPayService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;

@Controller
@RequestMapping("/history")
public class CoinTransactionController {

    @Autowired
    private CoinTransactionService coinTransactionService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getMyCoursePurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        if (userDetails == null || !(userDetails instanceof CustomUserDetails)) {
            return "redirect:/login";
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        Page<CoinTransactionDTO> transactionPage = coinTransactionService.getUserCoursePurchaseTransactions(userId, courseName, status, startDate, endDate, page, size);
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", transactionPage.getNumber());
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("totalItems", transactionPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("currentBalance", user != null ? user.getCoin() : 0);
        model.addAttribute("totalSpent", coinTransactionService.getTotalSpent(userId));
        model.addAttribute("totalCoursesPurchased", coinTransactionService.getTotalCoursesPurchased(userId));
        model.addAttribute("courseName", courseName);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/historyContent :: historyContent");
        return "homePage/index";
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public CoinTransactionDTO getTransactionDetail(@PathVariable("id") Long transactionId) {
        return coinTransactionService.getTransactionDetail(transactionId);
    }
}