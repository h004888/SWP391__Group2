package com.OLearning.repository;

import com.OLearning.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationsRepo extends JpaRepository<Notifications, Long> {

    // Lấy tất cả thông báo của user (instructor)
    List<Notifications> findByUser_UserIdOrderBySentAtDesc(Long userId);

    // Tìm kiếm thông báo theo tên khóa học (nếu có liên kết)
    List<Notifications> findByCourse_TitleContainingIgnoreCase( String keyword);

    Optional<Notifications> findById(Long id);
    @Modifying
    @Query("UPDATE Notifications n SET n.status = 'sent' WHERE n.user.userId = :userId AND n.status = 'failed'")
    int markAllAsReadByUserId(@Param("userId") Long userId);
}
