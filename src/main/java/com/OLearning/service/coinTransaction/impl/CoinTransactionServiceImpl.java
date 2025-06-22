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
    public CoinTransaction saveDepositTransactionAfterPayment(Long userId, BigDecimal amount, String transactionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CoinTransaction transaction = new CoinTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTransactionType("top_up");
        transaction.setStatus("completed");
        transaction.setNote("VNPay TransactionId: " + transactionId);
        transaction.setCreatedAt(LocalDateTime.now());

        user.setCoin(user.getCoin() + amount.doubleValue());
        userRepository.save(user);

        return coinTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public CoinTransaction processWithdrawal(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getCoin() < amount.doubleValue()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        CoinTransaction transaction = new CoinTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount.negate());
        transaction.setTransactionType("withdraw");
        transaction.setStatus("completed");
        transaction.setNote("Withdrawal processed");
        transaction.setCreatedAt(LocalDateTime.now());

        // Update user's coin balance
        user.setCoin(user.getCoin() - amount.doubleValue());
        userRepository.save(user);

        return coinTransactionRepository.save(transaction);
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
}