package com.OLearning.repository;



import com.OLearning.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    @Query("SELECT v FROM Voucher v JOIN v.voucherCourses vc WHERE vc.course.id = :courseId " +
            "AND v.isActive = true AND v.expiryDate >= CURRENT_DATE AND v.limitation > v.usedCount")
    List<Voucher> findValidVouchersForCourse(Long courseId);

    Page<Voucher> findByInstructor_UserIdAndCodeContainingIgnoreCase(Long instructorId, String code, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.isPublic = true AND v.isActive = true AND v.expiryDate >= :currentDate AND v.limitation > v.usedCount")
    List<Voucher> findAvailablePublicVouchers(LocalDate currentDate);

    List<Voucher> findByExpiryDateBeforeAndIsActiveTrue(LocalDate today);

    Page<Voucher> findByInstructor_UserIdAndExpiryDateBeforeOrIsActiveFalse(Long instructorId, LocalDate now, Pageable pageable);
    Page<Voucher> findByInstructor_UserIdAndExpiryDateAfterAndIsActiveTrue(Long instructorId, LocalDate now, Pageable pageable);

    // Sửa lại query để tránh lỗi logic OR, chỉ lấy voucher của instructor đó
    @Query("SELECT v FROM Voucher v WHERE v.instructor.userId = :instructorId AND (v.expiryDate < :now OR v.isActive = false)")
    Page<Voucher> findExpiredOrInactiveVouchersByInstructor(Long instructorId, LocalDate now, Pageable pageable);
}
