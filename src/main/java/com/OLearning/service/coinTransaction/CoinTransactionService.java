package com.OLearning.service.coinTransaction;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface CoinTransactionService {
    Page<CoinTransactionDTO> getUserCoinTransactions(Long userId, Pageable pageable);
}