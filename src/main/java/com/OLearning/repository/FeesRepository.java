package com.OLearning.repository;

import com.OLearning.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeesRepository extends JpaRepository<Fee, Long> {
    Fee findByMinEnrollmentsLessThanEqualAndMaxEnrollmentsGreaterThanEqual(Long enrollmentCount, Long enrollmentCount2);
}
