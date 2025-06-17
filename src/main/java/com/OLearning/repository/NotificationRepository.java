package com.OLearning.repository;

import com.OLearning.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Lấy tất cả thông báo của user (instructor)
    List<Notification> findByUser_UserIdOrderBySentAtDesc(Long userId);

    // Tìm kiếm thông báo theo tên khóa học (nếu có liên kết)
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND " +
            "(LOWER(n.course.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Notification> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);


    Optional<Notification> findById(Long id);
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'sent' WHERE n.user.userId = :userId AND n.status = 'failed'")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
