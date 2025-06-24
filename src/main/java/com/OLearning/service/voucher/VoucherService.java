package com.OLearning.service.voucher;

import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.dto.voucher.VoucherDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoucherService {
    VoucherDTO applyVoucher(String code, Long userId);
    List<UserVoucherDTO> getUserVouchers(Long userId);
    List<VoucherDTO> getValidVouchersForCourse(Long courseId);
    VoucherDTO useVoucher(Long voucherId, Long userId, Long courseId);
    List<UserVoucherDTO> getValidVouchersForCourseAndUser(Long courseId, Long userId);
}
