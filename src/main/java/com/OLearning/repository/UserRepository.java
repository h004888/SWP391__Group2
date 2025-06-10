
package com.OLearning.repository;

import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByRole_RoleId(Long roleId, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndRole_RoleId(String username, Long roleId, Pageable pageable);

    Boolean existsByEmail(String email);

    List<User> findByRole_RoleId(Long id);

//    @Query("SELECT u FROM User u WHERE " +
//            "u.role.roleId = :roleId " +
//            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(u.username) LIKE CONCAT('%', LOWER(:keyword), '%'))")
//    List<User> searchByNameAndRole(@Param("keyword") String keyword, @Param("roleId") Long roleId);
//
//    @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND " +
//            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<User> findByRoleAndKeyword(@Param("roleId") Long roleId,
//                                    @Param("keyword") String keyword,
//                                    Pageable pageable);
}
