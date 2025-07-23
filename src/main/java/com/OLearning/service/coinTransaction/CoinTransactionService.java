package com.OLearning.service.coinTransaction;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CoinTransactionService {
    Page<CoinTransactionDTO> getUserCoinTransactions(Long userId, Pageable pageable);

    Page<CoinTransactionDTO> filterAndSortTransactions(Long userId, String transactionType, String startDate, String endDate, int page, int size);

    Page<CoinTransactionDTO> getUserCoursePurchaseTransactions(Long userId, String courseName, String status, String startDate, String endDate, int page, int size);

    Double getTotalSpent(Long userId);

    long getTotalCoursesPurchased(Long userId);

    CoinTransactionDTO getTransactionDetail(Long transactionId);
}