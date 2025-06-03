package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByRole_RoleId(Long id);

    @Query("SELECT u FROM User u WHERE " +
            "u.role.roleId = :roleId " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(u.username) LIKE CONCAT('%', LOWER(:keyword), '%'))")
    List<User> searchByNameAndRole(@Param("keyword") String keyword, @Param("roleId") Long roleId);

}
