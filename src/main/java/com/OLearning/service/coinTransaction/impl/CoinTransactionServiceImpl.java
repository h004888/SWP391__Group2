package com.OLearning.service.coinTransaction.impl;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import com.OLearning.entity.OrderDetail;
import com.OLearning.entity.User;
import com.OLearning.mapper.coinTransaction.CoinTransactionMapper;
import com.OLearning.repository.CoinTransactionRepository;
import com.OLearning.repository.OrderDetailRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoinTransactionServiceImpl implements CoinTransactionService {

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CoinTransactionMapper coinTransactionMapper;

    @Override
    @Transactional
    public Page<CoinTransactionDTO> getUserCoinTransactions(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CoinTransaction> transactionPage = coinTransactionRepository.findByUserUserId(userId, pageableWithSort);
        if (transactionPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Lấy OrderDetail từ Order (sử dụng @EntityGraph nên đã có sẵn)
        Map<Long, List<OrderDetail>> orderDetailsMap = transactionPage.getContent().stream()
                .filter(transaction -> transaction.getOrder() != null)
                .collect(Collectors.toMap(
                        CoinTransaction::getTransactionId,
                        transaction -> transaction.getOrder().getOrderDetails() != null ?
                                transaction.getOrder().getOrderDetails() : List.of()
                ));

        List<CoinTransactionDTO> transactionDTOs = coinTransactionMapper.toDTOList(transactionPage.getContent(), orderDetailsMap);
        return new PageImpl<>(transactionDTOs, pageableWithSort, transactionPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<CoinTransactionDTO> filterAndSortTransactions(Long userId, String transactionType, String startDate, String endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CoinTransaction> transactionPage;
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                LocalDate actualStart = start.isBefore(end) ? start : end;
                LocalDate actualEnd = start.isBefore(end) ? end : start;
                LocalDateTime endDateTime = actualEnd.plusDays(1).atStartOfDay().minusSeconds(1);

                if (transactionType != null && !transactionType.trim().isEmpty()) {
                    transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndCreatedAtBetween(
                            userId, transactionType, actualStart.atStartOfDay(), endDateTime, pageable);
                } else {
                    transactionPage = coinTransactionRepository.findByUserUserIdAndCreatedAtBetween(
                            userId, actualStart.atStartOfDay(), endDateTime, pageable);
                }
            } catch (Exception e) {
                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                if (transactionType != null && !transactionType.trim().isEmpty()) {
                    transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, pageable);
                } else {
                    transactionPage = coinTransactionRepository.findByUserUserId(userId, pageable);
                }
            }
        } else {
            if (transactionType != null && !transactionType.trim().isEmpty()) {
                transactionPage = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, pageable);
            } else {
                transactionPage = coinTransactionRepository.findByUserUserId(userId, pageable);
            }
        }

        // Lấy OrderDetail từ Order
        Map<Long, List<OrderDetail>> orderDetailsMap = transactionPage.getContent().stream()
                .filter(transaction -> transaction.getOrder() != null)
                .collect(Collectors.toMap(
                        CoinTransaction::getTransactionId,
                        transaction -> transaction.getOrder().getOrderDetails() != null ?
                                transaction.getOrder().getOrderDetails() : List.of()
                ));

        List<CoinTransactionDTO> transactionDTOs = coinTransactionMapper.toDTOList(transactionPage.getContent(), orderDetailsMap);
        return new PageImpl<>(transactionDTOs, pageable, transactionPage.getTotalElements());
    }

    @Override
    @Transactional
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

        // Lấy OrderDetail từ Order
        Map<Long, List<OrderDetail>> orderDetailsMap = transactionPage.getContent().stream()
                .filter(transaction -> transaction.getOrder() != null)
                .collect(Collectors.toMap(
                        CoinTransaction::getTransactionId,
                        transaction -> transaction.getOrder().getOrderDetails() != null ?
                                transaction.getOrder().getOrderDetails() : List.of()
                ));

        // Ánh xạ và lọc theo courseName
        List<CoinTransactionDTO> dtos = coinTransactionMapper.toDTOList(transactionPage.getContent(), orderDetailsMap).stream()
                .filter(dto -> courseName == null || courseName.isEmpty() ||
                        (dto.getCourseName() != null && dto.getCourseName().toLowerCase().contains(courseName.toLowerCase())))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, transactionPage.getTotalElements());
    }

    @Override
    @Transactional
    public Double getTotalSpent(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        String transactionType = "COURSE_PURCHASE";
        List<CoinTransaction> transactions = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, Pageable.unpaged()).getContent();
        return transactions.stream()
                .filter(t -> t.getAmount() != null && t.getAmount() < 0)
                .map(t -> Math.abs(t.getAmount()))
                .reduce(0.0, Double::sum);
    }

    @Override
    @Transactional
    public long getTotalCoursesPurchased(Long userId) {
        String transactionType = "COURSE_PURCHASE";
        List<CoinTransaction> transactions = coinTransactionRepository.findByUserUserIdAndTransactionType(userId, transactionType, Pageable.unpaged()).getContent();
        return transactions.stream()
                .map(transaction -> {
                    List<OrderDetail> details = transaction.getOrder() != null ?
                            transaction.getOrder().getOrderDetails() : List.of();
                    return coinTransactionMapper.toDTO(transaction, details);
                })
                .map(CoinTransactionDTO::getCourseName)
                .filter(courseName -> courseName != null && !courseName.isEmpty())
                .distinct()
                .count();
    }

    @Override
    @Transactional
    public CoinTransactionDTO getTransactionDetail(Long transactionId) {
        CoinTransaction transaction = coinTransactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) return null;

        // Lấy OrderDetail từ Order
        List<OrderDetail> details = transaction.getOrder() != null ?
                transaction.getOrder().getOrderDetails() : List.of();

        return coinTransactionMapper.toDTO(transaction, details);
    }
}