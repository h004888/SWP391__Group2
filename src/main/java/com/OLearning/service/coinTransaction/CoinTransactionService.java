package com.OLearning.service.coinTransaction;

import java.math.BigDecimal;

public interface CoinTransactionService {
    void recordTransaction(Long userId, BigDecimal amount, String transactionType, String status, String refCode, String note);
}
