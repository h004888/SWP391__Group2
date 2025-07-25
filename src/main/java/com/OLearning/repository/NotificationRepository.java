package com.OLearning.repository;

import com.OLearning.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND (LOWER(n.course.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY (CASE WHEN n.status <> 'sent' THEN 0 ELSE 1 END), n.sentAt DESC")
    Page<Notification> findByUserIdAndKeywordUnreadFirst(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    Optional<Notification> findById(Long id);
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'sent' WHERE n.user.userId = :userId AND n.status = 'failed'")
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.type IN :types AND (:status IS NULL OR n.status = :status) ORDER BY (CASE WHEN n.status <> 'sent' THEN 0 ELSE 1 END), n.sentAt DESC")
    Page<Notification> findByUserIdAndTypesAndStatus(@Param("userId") Long userId, @Param("types") List<String> types, @Param("status") String status, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.type IN :types AND n.status = 'failed' ORDER BY n.sentAt DESC")
    Page<Notification> findUnreadByUserIdAndTypes(@Param("userId") Long userId, @Param("types") List<String> types, Pageable pageable);

    void deleteById(Long id);
    @Modifying
    @Query(value = "DELETE FROM Notifications WHERE userId = :userId AND status = :status", nativeQuery = true)
    void deleteByUser_UserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.status = 'failed'")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT n.type FROM Notification n")
    List<String> findAllNotificationTypes();

    @Query("SELECT DISTINCT n.type FROM Notification n WHERE n.user.userId = :userId")
    List<String> findAllNotificationTypesByUserId(@Param("userId") Long userId);
}
