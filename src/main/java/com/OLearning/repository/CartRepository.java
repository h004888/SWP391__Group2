package com.OLearning.repository;

import com.OLearning.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserUserId(Long userId);
    Optional<Cart> findByUser_UserId(Long userId);
}
