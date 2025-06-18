package com.OLearning.service.vnpay;

import com.OLearning.config.VNPayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    public String createOrder(HttpServletRequest req) throws  IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String orderType = "order-type";

        int amount = (int) req.getAttribute("amount");
        String bankCode = req.getParameter("bankCode");


        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Amount",  String.valueOf(amount));

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_Command", vnp_Command);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode", "VND");

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_Version", vnp_Version);


        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        Map<String, String> testFields = new HashMap<>();
        testFields.put("vnp_Amount", String.valueOf(amount));
        testFields.put("vnp_Command", "pay");
        testFields.put("vnp_CreateDate", vnp_CreateDate);
        testFields.put("vnp_CurrCode", "VND");
        testFields.put("vnp_ExpireDate", vnp_ExpireDate);
        testFields.put("vnp_IpAddr", "127.0.0.1");
        testFields.put("vnp_Locale", "vn");
        testFields.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        testFields.put("vnp_OrderType", "order-type");
        testFields.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        testFields.put("vnp_TmnCode", vnp_TmnCode);
        testFields.put("vnp_TxnRef", vnp_TxnRef);
        testFields.put("vnp_Version", "2.1.0");

        // Loại bỏ hash fields nếu có
        testFields.remove("vnp_SecureHash");
        testFields.remove("vnp_SecureHashType");

        String generatedHash = VNPayConfig.hashAllFields(testFields);
        System.out.println("generatedHash = " + generatedHash);

        System.out.println("HASHDATA = " + hashData);
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        System.out.println("SECURE_HASH = " + vnp_SecureHash);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        System.out.println("URL = " + paymentUrl);
        return paymentUrl;
    }

    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();

        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        // Remove hash params
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        if (vnp_SecureHash == null) return -1;

        String signValue = VNPayConfig.hashAllFields(fields); // Hàm này phải sort keys
        System.out.println("Generated signValue: " + signValue);
        System.out.println("Received vnp_SecureHash: " + vnp_SecureHash);

        if (signValue.equals(vnp_SecureHash)) {
            String status = request.getParameter("vnp_TransactionStatus");
            return "00".equals(status) ? 1 : 0;
        } else {
            System.out.println("Chữ ký không khớp.");
            return -1;
        }
    }
}
