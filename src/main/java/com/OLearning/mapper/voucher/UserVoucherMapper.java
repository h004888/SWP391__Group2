package com.OLearning.mapper.voucher;

import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.entity.UserVoucher;
import org.springframework.stereotype.Component;

@Component
public class UserVoucherMapper {
    public UserVoucherDTO toUserVoucherDTO(UserVoucher userVoucher) {
        UserVoucherDTO dto = new UserVoucherDTO();
        dto.setId(userVoucher.getId());
        dto.setVoucherId(userVoucher.getVoucher().getVoucherId());
        dto.setVoucherCode(userVoucher.getVoucher().getCode());
        dto.setDiscount(userVoucher.getVoucher().getDiscount());
        dto.setExpiryDate(userVoucher.getVoucher().getExpiryDate());
        dto.setIsUsed(userVoucher.getIsUsed());
        dto.setUsedDate(userVoucher.getUsedDate());
        return dto;
    }
}
