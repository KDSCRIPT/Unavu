package com.unavu.notification.service.impl;

import com.unavu.common.web.exception.ResourceNotFoundException;
import com.unavu.notification.entity.Notification;
import com.unavu.notification.repository.NotificationRepository;
import com.unavu.notification.service.NotificationDispatcher;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.UserFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDispatcher dispatcher;
    private final UserFeignClient userFeignClient;

    @Override
    public void createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Notification stored id={}", saved.getId());

        dispatcher.sendRealtime(saved);

        // fetch email and send
        try {
            ResponseEntity<String> response = userFeignClient.getUserEmail(saved.getUserId());
            if (response != null && response.getBody() != null) {
                dispatcher.sendEmail(response.getBody(), buildSubject(notification), notification.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to fetch email for userId={}: {}", notification.getUserId(), e.getMessage());
        }
    }

    private String buildSubject(Notification notification) {
        return switch (notification.getNotificationType()) {
            case REVIEW_CREATED -> "Someone reviewed a restaurant you follow";
            case USER_FOLLOWED -> "You have a new follower";
            case RESTAURANT_CREATED -> "A new restaurant was added";
            case LIST_CREATED -> "A new list was created";
            case LIST_ITEM_ADDED -> "A new item was added to a list";
        };
    }
    @Override
    public Page<Notification> getUserNotifications(String userId, Pageable pageable) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification","notificationId",notificationId));

        notification.setRead(true);

        notificationRepository.save(notification);

    }

    @Override
    public void markAllAsRead(String userId) {

        Page<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged());

        notifications.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(notifications.getContent());

    }
}
