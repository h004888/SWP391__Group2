package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.mapper.instructorDashBoard.NotificationsMapper;
import com.OLearning.repository.instructorDashBoard.NotificationsRepo;
import com.OLearning.service.instructorDashBoard.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationsServiceImpl implements NotificationsService {

    @Autowired
    private NotificationsRepo notificationsRepo;

    @Autowired
    private NotificationsMapper notificationsMapper;

    @Override
    public List<NotificationsDTO> getNotificationsByUserId(Long userId) {
        return notificationsRepo.findByUser_UserIdOrderBySentAtDesc(userId)
                .stream()
                .map(notificationsMapper::toDTO)
                .toList();
    }

    @Override
    public List<NotificationsDTO> searchNotifications(Long userId, String keyword) {
        return notificationsRepo.findByUser_UserIdAndCourse_TitleContainingIgnoreCase(userId, keyword)
                .stream()
                .map(notificationsMapper::toDTO)
                .toList();
    }
}
