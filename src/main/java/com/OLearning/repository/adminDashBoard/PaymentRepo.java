package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Integer> {

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findAll();

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findByUserUsernameContaining(String username);

    @EntityGraph(attributePaths = {"user", "courses"})
    @Query("SELECT p FROM Payment p WHERE EXISTS (SELECT 1 FROM p.courses c WHERE c.title LIKE :courseNamePattern)")
    List<Payment> findByCourseNamePattern(@Param("courseNamePattern") String courseNamePattern);

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findAllByOrderByAmountAsc();

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findAllByOrderByAmountDesc();

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findAllByOrderByPaymentDateAsc();

    @EntityGraph(attributePaths = {"user", "courses"})
    List<Payment> findAllByOrderByPaymentDateDesc();
}
