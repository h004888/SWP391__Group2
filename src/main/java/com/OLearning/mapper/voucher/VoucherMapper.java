package com.OLearning.mapper.voucher;

import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.entity.Voucher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class VoucherMapper {
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherId(voucher.getVoucherId());
        dto.setCode(voucher.getCode());
        dto.setDiscount(voucher.getDiscount());
        dto.setExpiryDate(voucher.getExpiryDate());
        dto.setLimitation(voucher.getLimitation());
        dto.setUsedCount(voucher.getUsedCount());
        dto.setIsActive(voucher.getIsActive());
        dto.setIsGlobal(voucher.getIsGlobal());
        dto.setCreatedDate(voucher.getCreatedDate());
        
        if (voucher.getVoucherCourses() != null && !voucher.getVoucherCourses().isEmpty()) {
            dto.setCourseName(voucher.getVoucherCourses().stream()
                    .map(voucherCourse -> voucherCourse.getCourse().getCourseId())
                    .collect(Collectors.toList()));
        } else {
            dto.setCourseName(new ArrayList<>());
        }
        
        return dto;
    }
}
