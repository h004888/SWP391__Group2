package com.OLearning.repository;

import com.OLearning.entity.TermsAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionRepository extends JpaRepository<TermsAndCondition, Long> {
    @Query("SELECT COALESCE(MAX(t.displayOrder), 0) FROM TermsAndCondition t")
    int findMaxDisplayOrder();

    // Fetch terms and conditions for a specific role or for all
    @Query("SELECT t FROM TermsAndCondition t WHERE t.roleTarget = :roleTarget OR t.roleTarget = 'ALL' ORDER BY t.displayOrder ASC")
    java.util.List<TermsAndCondition> findByRoleTargetOrAll(@org.springframework.data.repository.query.Param("roleTarget") String roleTarget);
}
