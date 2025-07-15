package com.OLearning.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class VietQRService {
    @Value("${sepay.account_number}")
    private String accountNumber;

    @Value("${sepay.bank_code}")
    private String bankCode;



    public String generateSePayQRUrl(double amount, String description) {
        return String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%d&des=%s&template=compact2&download=0",
                accountNumber, bankCode, (int)amount, description);
    }
} 