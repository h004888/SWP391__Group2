package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.username) LIKE CONCAT('%', :keyword, '%')) AND " +
            "(:roleId IS NULL OR u.role.roleId = :roleId)")
    List<User> searchByKeyword(@Param("keyword") String keyword,
                                  @Param("roleId") Integer roleId);

}
