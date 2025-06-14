package com.OLearning.repository;

import com.OLearning.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
}
