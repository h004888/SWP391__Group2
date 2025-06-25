package com.OLearning.repository;

import com.OLearning.entity.TermsAndCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionRepository extends JpaRepository<TermsAndCondition, Long> {
}
