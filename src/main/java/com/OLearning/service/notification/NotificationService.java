package com.OLearning.service.notification;

import com.OLearning.dto.notification.NotificationDTO;
import com.OLearning.entity.Notification;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    Notification sendMess(Notification notification);
    void rejectCourseMess(NotificationDTO dto, boolean allowResubmission);
}
