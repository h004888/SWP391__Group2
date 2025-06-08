package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashBoard.NotificationsDTO;
import com.OLearning.entity.Notifications;
import com.OLearning.mapper.instructorDashBoard.NotificationsMapper;
import com.OLearning.repository.instructorDashBoard.NotificationsRepo;
import com.OLearning.service.instructorDashBoard.NotificationsService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationsDTO> searchNotifications( String keyword) {
        return notificationsRepo.findByCourse_TitleContainingIgnoreCase( keyword)
                .stream()
                .map(notificationsMapper::toDTO)
                .toList();
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationsRepo.findById(notificationId).ifPresent(notification -> {
            notification.setStatus("sent"); // true = read
            notificationsRepo.save(notification);
        });

    }

    @Override
    public Optional<Notifications> findById(Long id) {
        return notificationsRepo.findById(id);
    }
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationsRepo.markAllAsReadByUserId(userId);
    }
//    @Override
//    public NotificationsDTO getNotificationDetail(Long notificationId, Long userId) {
//        Optional<Notifications> opt = notificationsRepo.findById(notificationId);
//        if (opt.isPresent()) {
//            Notifications notification = opt.get();
//            if (!notification.getUser().getUserId().equals(userId)) {
//                throw new SecurityException("Unauthorized access to notification");
//            }
//
//            if (!notification.isStatus()) {
//                notification.setStatus(true);
//                notificationsRepo.save(notification);
//            }
//
//            return notificationsMapper.toDTO(notification);
//        } else {
//            throw new IllegalArgumentException("Notification not found");
//        }
//    }
}
