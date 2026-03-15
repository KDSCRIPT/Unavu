package com.unavu.notification.service;

import com.unavu.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(Notification notification);

    Page<Notification> getUserNotifications(
            String userId,
            Pageable pageable
    );

    void markAsRead(Long notificationId);

    void markAllAsRead(String userId);

}