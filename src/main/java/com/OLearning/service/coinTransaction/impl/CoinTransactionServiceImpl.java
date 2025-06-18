package com.OLearning.service.coinTransaction.impl;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import com.OLearning.entity.OrderDetail;
import com.OLearning.entity.Orders;
import com.OLearning.mapper.coinTransaction.CoinTransactionMapper;
import com.OLearning.repository.CoinTransactionRepository;
import com.OLearning.repository.OrderDetailRepository;
import com.OLearning.repository.OrdersRepository;

import com.OLearning.service.coinTransaction.CoinTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoinTransactionServiceImpl implements CoinTransactionService {

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CoinTransactionMapper coinTransactionMapper;

    @Override
    public Page<CoinTransactionDTO> getUserCoinTransactions(Long userId, Pageable pageable) {
        Page<CoinTransaction> transactionPage = coinTransactionRepository.findByUserUserId(userId, pageable);
        List<CoinTransaction> transactions = transactionPage.getContent();
        List<CoinTransactionDTO> transactionDTOs = coinTransactionMapper.toDTOList(transactions);

        for (int i = 0; i < transactions.size(); i++) {
            CoinTransaction transaction = transactions.get(i);
            CoinTransactionDTO dto = transactionDTOs.get(i);

            // Set course name for non-deposit/withdrawal transactions
            if (!"top_up".equalsIgnoreCase(transaction.getTransactionType()) &&
                    !"withdraw".equalsIgnoreCase(transaction.getTransactionType())) {
                Optional<Orders> order = ordersRepository.findByUserUserIdAndAmount(userId, transaction.getAmount().doubleValue());
                if (order.isPresent()) {
                    List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.get().getOrderId());
                    if (!orderDetails.isEmpty()) {
                        dto.setCourseName(orderDetails.get(0).getCourse() != null ?
                                orderDetails.get(0).getCourse().getTitle() : null);
                    }
                }
            } else {
                dto.setCourseName(null);
            }
        }

        return new PageImpl<>(transactionDTOs, pageable, transactionPage.getTotalElements());
    }
}