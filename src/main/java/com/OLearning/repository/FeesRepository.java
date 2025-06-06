package com.OLearning.repository;

import com.OLearning.entity.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Long> {
    Fees findByMinEnrollmentsLessThanEqualAndMaxEnrollmentsGreaterThanEqual(Long enrollmentCount, Long enrollmentCount2);
}
