package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.entity.Notification;
import com.unavu.notification.mapper.NotificationMapper;
import com.unavu.notification.service.NotificationDispatcher;
import com.unavu.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialGraphNotificationHandler {

    private final NotificationDispatcher dispatcher;
    private final NotificationService notificationService;

    public void handleUserFollowedEvent(NotificationDto event) {

        log.info("User follow event {}", event);

        Notification notification = NotificationMapper.mapToNotification(event);

        notificationService.createNotification(notification);
    }
}