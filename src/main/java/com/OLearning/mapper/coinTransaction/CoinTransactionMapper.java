package com.OLearning.mapper.coinTransaction;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.CoinTransaction;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import com.OLearning.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
                if ("course_purchase".equals(coinTransaction.getTransactionType())) {
                    OrderDetail detail = details.get(0);
                    if (detail.getCourse() != null) {
                        dto.setCourseName(detail.getCourse().getTitle());
                        dto.setInstructorName(detail.getCourse().getInstructor() != null ? detail.getCourse().getInstructor().getFullName() : null);
                        dto.setOriginalPrice(detail.getCourse().getPrice());
                        dto.setDiscountedPrice(detail.getUnitPrice());
                    }
                    // Xác định phương thức thanh toán dựa trên thông tin có sẵn
                    String paymentMethod = determinePaymentMethod(coinTransaction, coinTransaction.getOrder());
                    dto.setPaymentMethod(paymentMethod);
                } else {
                    if (coinTransaction.getAmount() != null && coinTransaction.getAmount() < 0) {
                        for (OrderDetail detail : details) {
                            if (detail.getUnitPrice() != null &&
                                detail.getUnitPrice().doubleValue() == Math.abs(coinTransaction.getAmount().doubleValue()) &&
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
        } else {
            // Nếu không có Order, xác định phương thức thanh toán dựa trên transaction type và note
            String paymentMethod = determinePaymentMethod(coinTransaction, null);
            dto.setPaymentMethod(paymentMethod);
        }
        return dto;
    }

    public List<CoinTransactionDTO> toDTOList(List<CoinTransaction> coinTransactions) {
        return coinTransactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String determinePaymentMethod(CoinTransaction transaction, Order order) {
        // Kiểm tra note của transaction trước
        String note = transaction.getNote();
        if (note != null) {
            if (note.contains("VNPay")) {
                return "VNPay";
            } else if (note.contains("QR")) {
                return "QR Banking";
            }
        }

        if (order != null) {
            String description = order.getDescription();
            if (description != null) {
                if (description.contains("VNPay") || description.contains("vnp_")) {
                    return "VNPay";
                } else if (description.contains("QR") || description.contains("qr")) {
                    return "QR Banking";
                }
            }

            String refCode = order.getRefCode();
            if (refCode != null) {
                if (refCode.matches("\\d{10,}")) {
                    return "VNPay";
                }
            }
        }

        String transactionType = transaction.getTransactionType();
        if ("top_up".equals(transactionType)) {
            if (note != null && note.contains("VNPay")) {
                return "VNPay";
            } else if (note != null && note.contains("QR")) {
                return "QR Banking";
            } else {
                return "VNPay";
            }
        } else if ("course_purchase".equals(transactionType)) {
            return "Ví nội bộ";
        } else if ("withdraw".equals(transactionType)) {
            return "Rút tiền";
        }

        return "Ví nội bộ";
    }
}

