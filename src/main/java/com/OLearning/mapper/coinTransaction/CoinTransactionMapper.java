package com.OLearning.mapper.coinTransaction;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CoinTransactionMapper {
    public CoinTransactionDTO toDTO(CoinTransaction coinTransaction) {
        CoinTransactionDTO dto = new CoinTransactionDTO();
        dto.setTransactionId(coinTransaction.getTransactionId());
        dto.setAmount(coinTransaction.getAmount());
        dto.setTransactionType(coinTransaction.getTransactionType());
        dto.setStatus(coinTransaction.getStatus());
        dto.setNote(coinTransaction.getNote());
        dto.setCreatedAt(coinTransaction.getCreatedAt());
        return dto;
    }

    public List<CoinTransactionDTO> toDTOList(List<CoinTransaction> coinTransactions) {
        return coinTransactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

