package com.OLearning.repository;

import com.OLearning.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    @Query("SELECT v FROM Voucher v JOIN v.voucherCourses vc WHERE vc.course.id = :courseId " +
            "AND v.isActive = true AND v.expiryDate > CURRENT_TIMESTAMP " +
            "AND (v.limitation > v.usedCount OR v.limitation IS NULL)")
    List<Voucher> findValidVouchersForCourse(Long courseId);
}
