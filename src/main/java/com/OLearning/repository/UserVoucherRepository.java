package com.OLearning.repository;

import com.OLearning.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {
    List<UserVoucher> findByUser_UserIdAndIsUsedFalse(Long userId);
    List<UserVoucher> findByUser_UserIdAndIsUsedFalseOrderByIdDesc(Long userId);
    Optional<UserVoucher> findByUser_UserIdAndVoucher_VoucherId(Long userId, Long voucherId);
}
