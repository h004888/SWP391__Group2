package com.OLearning.service.coinTransaction.impl;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import com.OLearning.entity.User;
import com.OLearning.mapper.coinTransaction.CoinTransactionMapper;
import com.OLearning.repository.CoinTransactionRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.coinTransaction.CoinTransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoinTransactionServiceImpl implements CoinTransactionService {

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoinTransactionMapper coinTransactionMapper;

    @Override
    @Transactional
    public Page<CoinTransactionDTO> getUserCoinTransactions(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Create new pageable with sorting
        Pageable pageableWithSort = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
        
        Page<CoinTransaction> transactionPage = coinTransactionRepository.findByUserUserId(userId, pageableWithSort);
        if (transactionPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<CoinTransaction> transactions = transactionPage.getContent();
        List<CoinTransactionDTO> transactionDTOs = coinTransactionMapper.toDTOList(transactions);

        return new PageImpl<>(transactionDTOs, pageableWithSort, transactionPage.getTotalElements());
    }


    @Override
    @Transactional
    public Page<CoinTransactionDTO> filterAndSortTransactions(Long userId, String transactionType, String startDate, String endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                
                // Handle case where endDate is smaller than startDate
                LocalDate actualStart = start.isBefore(end) ? start : end;
                LocalDate actualEnd = start.isBefore(end) ? end : start;
                
                // Set end date to end of day
                LocalDateTime endDateTime = actualEnd.plusDays(1).atStartOfDay().minusSeconds(1);

                Page<CoinTransaction> transactionPage;
                if (transactionType != null && !transactionType.trim().isEmpty()) {
                    transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndCreatedAtBetween(
                            userId,
                            transactionType,
                            actualStart.atStartOfDay(),
                            endDateTime,
                            pageable
                    );
                } else {
                    transactionPage = coinTransactionRepository.findByUserUserIdAndCreatedAtBetween(
                            userId,
                            actualStart.atStartOfDay(),
                            endDateTime,
                            pageable
                    );
                }
                return transactionPage.map(coinTransactionMapper::toDTO);
            } catch (Exception e) {
                // If date parsing fails, fall back to default filtering
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            }
        }

        Page<CoinTransaction> transactionPage;
        if (transactionType != null && !transactionType.trim().isEmpty()) {
            transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, pageable);
        } else {
            transactionPage = coinTransactionRepository.findByUserUserId(userId, pageable);
        }

        return transactionPage.map(coinTransactionMapper::toDTO);
    }

    public Page<CoinTransactionDTO> getUserCoursePurchaseTransactions(Long userId, String courseName, String status, String startDate, String endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String transactionType = "COURSE_PURCHASE";
        Page<CoinTransaction> transactionPage;
        LocalDateTime start = null, end = null;
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).plusDays(1).atStartOfDay().minusSeconds(1);
        }
        if (status != null && !status.trim().isEmpty() && start != null && end != null) {
            transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndStatusAndCreatedAtBetween(userId, transactionType, status, start, end, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndStatus(userId, transactionType, status, pageable);
        } else if (start != null && end != null) {
            transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndCreatedAtBetween(userId, transactionType, start, end, pageable);
        } else {
            transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, pageable);
        }
        List<CoinTransactionDTO> dtos = transactionPage.getContent().stream()
            .map(coinTransactionMapper::toDTO)
            .filter(dto -> courseName == null || courseName.isEmpty() || (dto.getCourseName() != null && dto.getCourseName().toLowerCase().contains(courseName.toLowerCase())))
            .toList();
        return new PageImpl<>(dtos, pageable, transactionPage.getTotalElements());
    }

    public Double getTotalSpent(Long userId) {
        String transactionType = "COURSE_PURCHASE";
        List<CoinTransaction> transactions = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, Pageable.unpaged()).getContent();
        return transactions.stream()
            .filter(t -> t.getAmount() != null && t.getAmount() < 0)
            .map(t -> Math.abs(t.getAmount()))
            .reduce(0.0, Double::sum);
    }
    public long getTotalCoursesPurchased(Long userId) {
        String transactionType = "COURSE_PURCHASE";
        List<CoinTransaction> transactions = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, Pageable.unpaged()).getContent();
        return transactions.stream().map(coinTransactionMapper::toDTO).map(CoinTransactionDTO::getCourseName).distinct().count();
    }

    // Lấy chi tiết giao dịch
    public CoinTransactionDTO getTransactionDetail(Long transactionId) {
        CoinTransaction transaction = coinTransactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) return null;
        return coinTransactionMapper.toDTO(transaction);
    }
}