package com.OLearning.repository;

import com.OLearning.entity.Orders;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findAll(Pageable pageable);

    List<Orders> findByUserUsernameContaining(String username);

    Page<Orders> findByUserUsernameContaining(String username, Pageable pageable);

    List<Orders> findAllByOrderByAmountAsc();

    List<Orders> findAllByOrderByAmountDesc();

    List<Orders> findAllByOrderByOrderDateAsc();

    List<Orders> findAllByOrderByOrderDateDesc();

    Page<Orders> findByUserUsernameContainingOrderByAmountAsc(String username, Pageable pageable);

    Page<Orders> findByUserUsernameContainingOrderByAmountDesc(String username, Pageable pageable);

    Page<Orders> findByUserUsernameContainingOrderByOrderDateAsc(String username, Pageable pageable);

    Page<Orders> findByUserUsernameContainingOrderByOrderDateDesc(String username, Pageable pageable);

    List<Orders> findByUserAndStatus(User user, String status);
}