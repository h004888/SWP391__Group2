package com.OLearning.service.voucher;

import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.voucher.VoucherStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface VoucherService {
    VoucherDTO applyVoucher(String code, Long userId);
    List<UserVoucherDTO> getUserVouchers(Long userId);
    List<UserVoucherDTO> getUserVouchersSortedByLatest(Long userId);
    List<VoucherDTO> getValidVouchersForCourse(Long courseId);
    VoucherDTO useVoucher(Long voucherId, Long userId, Long courseId);
    List<UserVoucherDTO> getValidVouchersForCourseAndUser(Long courseId, Long userId);
    void useVoucherForUserAndCourse(Long voucherId, Long userId);
    List<CourseDTO> getValidCoursesForVoucherAndUser(Long voucherId, Long userId);
    VoucherStatsDTO getVoucherStatsForInstructor(Long instructorId, String search);
    List<String> getValidCourseTitlesForVoucher(Long voucherId);
    VoucherDTO createVoucherForInstructor(VoucherDTO voucherDTO, Long instructorId, List<Long> selectedCourses);
    Map<String, Object> getFilteredVouchersForInstructor(Long instructorId, String keyword, String tabType, int page, int size);
    Page<VoucherDTO> getVouchersForInstructor(Long instructorId, String keyword, String status, int page, int size);
    List<VoucherDTO> getPublicVouchers();
    void claimPublicVoucher(Long voucherId, Long userId);
    List<CourseDTO> getCoursesForVoucher(Long voucherId);

    Map<String, Object> applyVoucherToCourse(Long userId, Long courseId, Long voucherId);
}