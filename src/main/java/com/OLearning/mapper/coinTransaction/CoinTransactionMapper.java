package com.OLearning.mapper.coinTransaction;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import com.OLearning.entity.OrderDetail;
import com.OLearning.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CoinTransactionMapper {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public CoinTransactionDTO toDTO(CoinTransaction coinTransaction) {
        CoinTransactionDTO dto = new CoinTransactionDTO();
        dto.setTransactionId(coinTransaction.getTransactionId());
        dto.setAmount(coinTransaction.getAmount());
        dto.setTransactionType(coinTransaction.getTransactionType());
        dto.setStatus(coinTransaction.getStatus());
        dto.setNote(coinTransaction.getNote());
        dto.setCreatedAt(coinTransaction.getCreatedAt());
        if (coinTransaction.getOrder() != null) {
            dto.setRefCode(coinTransaction.getOrder().getRefCode());
            List<OrderDetail> details = orderDetailRepository.findByOrder(coinTransaction.getOrder());
            if (details != null && !details.isEmpty()) {
                if (coinTransaction.getAmount() != null && coinTransaction.getAmount().signum() < 0) {
                    for (OrderDetail detail : details) {
                        if (detail.getUnitPrice() != null &&
                            detail.getUnitPrice().doubleValue() == coinTransaction.getAmount().abs().doubleValue() &&
                            detail.getCourse() != null) {
                            dto.setCourseName(detail.getCourse().getTitle());
                            break;
                        }
                    }
                } else {
                    for (OrderDetail detail : details) {
                        if (detail.getCourse() != null) {
                            dto.setCourseName(detail.getCourse().getTitle());
                            break;
                        }
                    }
                }
            }
        }
        return dto;
    }

    public List<CoinTransactionDTO> toDTOList(List<CoinTransaction> coinTransactions) {
        return coinTransactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

