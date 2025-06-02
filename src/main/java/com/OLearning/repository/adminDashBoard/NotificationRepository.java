package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notifications, Long> {

}
