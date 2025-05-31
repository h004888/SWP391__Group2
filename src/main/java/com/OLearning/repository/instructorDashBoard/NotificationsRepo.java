package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepo extends JpaRepository<Notifications, Long> {

    // Lấy tất cả thông báo của user (instructor)
    List<Notifications> findByUser_UserIdOrderBySentAtDesc(Long userId);

    // Tìm kiếm thông báo theo tên khóa học (nếu có liên kết)
    List<Notifications> findByUser_UserIdAndCourse_TitleContainingIgnoreCase(Long userId, String keyword);
}
