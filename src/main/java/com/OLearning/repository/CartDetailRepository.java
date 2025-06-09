package com.OLearning.repository;

import com.OLearning.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    CartDetail findByCartCartIdAndCourseCourseId(Long cartId, Long courseId);
}
