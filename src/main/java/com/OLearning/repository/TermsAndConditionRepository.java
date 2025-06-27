package com.OLearning.repository;

import com.OLearning.entity.TermsAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionRepository extends JpaRepository<TermsAndCondition, Long> {
    @Query("SELECT COALESCE(MAX(t.displayOrder), 0) FROM TermsAndCondition t")
    int findMaxDisplayOrder();
}
