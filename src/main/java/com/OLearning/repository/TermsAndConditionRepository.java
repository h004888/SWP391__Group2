package com.OLearning.repository;

import com.OLearning.entity.TermsAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsAndConditionRepository extends JpaRepository<TermsAndCondition, Long> {
    @Query("SELECT COALESCE(MAX(t.displayOrder), 0) FROM TermsAndCondition t")
    int findMaxDisplayOrder();

    List<TermsAndCondition> findByRoleTargetInIgnoreCaseOrderByDisplayOrderAsc(List<String> roles);

    // Fetch terms and conditions for a specific role or for all
    @Query("SELECT t FROM TermsAndCondition t WHERE t.roleTarget = :roleTarget OR t.roleTarget = 'ALL' ORDER BY t.displayOrder ASC")
    java.util.List<TermsAndCondition> findByRoleTargetOrAll(@Param("roleTarget") String roleTarget);
}
