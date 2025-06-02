package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.NotificationDTO;
import com.OLearning.entity.Notifications;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    Notifications sendMess(Notifications notifications);
    void rejectCourseMess(NotificationDTO dto, boolean allowResubmission);
}
