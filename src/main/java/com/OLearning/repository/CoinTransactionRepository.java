package com.OLearning.repository;

import com.OLearning.entity.CoinTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    Page<CoinTransaction> findByUserUserId(Long userId, Pageable pageable);

    Page<CoinTransaction> findByUserUserIdAndTransactionType(Long userId, String transactionType, Pageable pageable);

    // Filter by date range
    Page<CoinTransaction> findByUserUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Filter by transaction type and date range
    Page<CoinTransaction> findByUserUserIdAndTransactionTypeAndCreatedAtBetween(Long userId, String transactionType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Lọc theo trạng thái cho giao dịch mua khóa học
    Page<CoinTransaction> findByUserUserIdAndTransactionTypeAndStatus(Long userId, String transactionType, String status, Pageable pageable);

    // Lọc theo trạng thái và ngày cho giao dịch mua khóa học
    Page<CoinTransaction> findByUserUserIdAndTransactionTypeAndStatusAndCreatedAtBetween(Long userId, String transactionType, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

}
