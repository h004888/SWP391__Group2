package com.OLearning.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class VietQRService {
    @Value("${sepay.account_number}")
    private String accountNumber;

    @Value("${sepay.bank_code}")
    private String bankCode;



    /**
     * Tạo URL QR code SePay cho thanh toán
     * @param amount Số tiền
     * @param description Nội dung chuyển khoản (mã đơn hàng)
     * @return link QR SePay
     */
    public String generateSePayQRUrl(double amount, String description) {
        return String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%d&des=%s&template=compact2&download=0",
                accountNumber, bankCode, (int)amount, description);
    }
} 