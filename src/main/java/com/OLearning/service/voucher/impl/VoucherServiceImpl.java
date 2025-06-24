package com.OLearning.service.voucher.impl;

import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.entity.User;
import com.OLearning.entity.UserVoucher;
import com.OLearning.entity.Voucher;
import com.OLearning.mapper.voucher.UserVoucherMapper;
import com.OLearning.mapper.voucher.VoucherMapper;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.UserVoucherRepository;
import com.OLearning.repository.VoucherRepository;
import com.OLearning.service.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private UserVoucherMapper userVoucherMapper;

    @Override
    @Transactional
    public VoucherDTO applyVoucher(String code, Long userId) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Voucher is invalid or expired");
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            throw new RuntimeException("Voucher usage limit reached");
        }

        java.util.Optional<UserVoucher> existing = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucher.getVoucherId());
        if (existing.isPresent()) {
            throw new RuntimeException("You have already entered this voucher!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setUser(user);
        userVoucher.setVoucher(voucher);
        userVoucher.setIsUsed(false);
        userVoucherRepository.save(userVoucher);

        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<UserVoucherDTO> getUserVouchers(Long userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId);
        return userVouchers.stream()
                .map(userVoucherMapper::toUserVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> getValidVouchersForCourse(Long courseId) {
        List<Voucher> vouchers = voucherRepository.findValidVouchersForCourse(courseId);
        return vouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VoucherDTO useVoucher(Long voucherId, Long userId, Long courseId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        UserVoucher userVoucher = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId).stream()
                .filter(uv -> uv.getVoucher().getVoucherId().equals(voucherId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User does not have this voucher"));

        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Voucher is invalid or expired");
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            throw new RuntimeException("Voucher usage limit reached");
        }

        userVoucher.setIsUsed(true);
        userVoucher.setUsedDate(LocalDateTime.now());
        userVoucherRepository.save(userVoucher);

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<UserVoucherDTO> getValidVouchersForCourseAndUser(Long courseId, Long userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId);
        List<UserVoucher> valid = userVouchers.stream()
            .filter(uv -> uv.getVoucher().getVoucherCourses().stream()
                .anyMatch(vc -> vc.getCourse().getCourseId().equals(courseId)))
            .collect(Collectors.toList());
        return valid.stream().map(userVoucherMapper::toUserVoucherDTO).collect(Collectors.toList());
    }
}
