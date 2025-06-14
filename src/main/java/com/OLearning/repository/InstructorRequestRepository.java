package com.OLearning.repository;

import com.OLearning.entity.InstructorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRequestRepository extends JpaRepository<InstructorRequest, Long> {

    Page<InstructorRequest> findAll(Pageable pageable);

    @Query("SELECT ir FROM InstructorRequest ir WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(ir.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ir.user.personalSkill) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR ir.status = :status)")
    Page<InstructorRequest> findByKeywordAndStatus(
        @Param("keyword") String keyword,
        @Param("status") String status,
        Pageable pageable
    );

}
