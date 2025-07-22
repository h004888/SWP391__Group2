package com.OLearning.repository;

import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
//    @Query("SELECT u FROM User u WHERE u.email = :email")
//    Optional<User> findEmail(@Param("email") String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleId = :roleId")
    List<User> findByRoleId(@Param("roleId") Long roleId);


    @Query("SELECT u FROM User u WHERE u.userId = :id")
    Optional<User> findById(Long id);

    Page<User> findAll(Pageable pageable);

    Page<User> findByRole_RoleId(Long roleId, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndRole_RoleId(String username, Long roleId, Pageable pageable);

    List<User> findByRole_RoleId(Long id);

    Page<User> findByRole_RoleIdAndStatus(Long roleId, boolean status, Pageable pageable);

    Page<User> findByUsernameContainingIgnoreCaseAndRole_RoleIdAndStatus(String username, Long roleId, boolean status, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleId = :roleId ORDER BY SIZE(u.courses) DESC")
    Page<User> findInstructorsByRoleIdOrderByCourseCountDesc(@Param("roleId") Long roleId, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleId = :roleId AND LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY SIZE(u.courses) DESC")
    Page<User> findInstructorsByRoleIdAndKeywordOrderByCourseCountDesc(@Param("roleId") Long roleId, @Param("keyword") String keyword, Pageable pageable);

    Page<User> findByRole_RoleIdIn(List<Long> roleIds, Pageable pageable);
    Page<User> findByRole_RoleIdInAndStatus(List<Long> roleIds, boolean status, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseAndRole_RoleIdIn(String username, List<Long> roleIds, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCaseAndRole_RoleIdInAndStatus(String username, List<Long> roleIds, boolean status, Pageable pageable);

}