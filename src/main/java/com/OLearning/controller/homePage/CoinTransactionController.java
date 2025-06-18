package com.OLearning.controller.homePage;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.User;

import com.OLearning.service.coinTransaction.CoinTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/history")
public class CoinTransactionController {

    @Autowired
    private CoinTransactionService coinTransactionService;

    @GetMapping
    public String getMyCoinTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Assuming UserDetails is an instance of User
        if (!(userDetails instanceof User)) {
            model.addAttribute("error", "Invalid user details");
            return "homePage/history"; // Adjust to your error view
        }

        User user = (User) userDetails;
        Pageable pageable = PageRequest.of(page, size);
        Page<CoinTransactionDTO> transactionPage = coinTransactionService.getUserCoinTransactions(user.getUserId(), pageable);

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", transactionPage.getNumber());
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("totalItems", transactionPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "homePage/history"; // Adjust to your view name
    }
}