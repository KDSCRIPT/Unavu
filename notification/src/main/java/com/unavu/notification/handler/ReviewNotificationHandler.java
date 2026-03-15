package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.entity.Notification;
import com.unavu.notification.mapper.NotificationMapper;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewNotificationHandler {

    private final NotificationService notificationService;
    private final SocialGraphFeignClient socialGraphFeignClient;

    public void handleReviewCreatedEvent(NotificationDto event) {

        ResponseEntity<List<String>> response = socialGraphFeignClient.findFollowerIds(event.actorId());
        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            log.info("No followers found for user {}", event.actorId());
            return;
        }

        List<String> followerIds = response.getBody();

        if (followerIds.isEmpty()) {
            log.info("No followers found for user {}", event.actorId());
            return;
        }

        for (String followerId : followerIds) {

            NotificationDto followerNotification = new NotificationDto(
                    event.notificationType(),
                    event.actorId(),
                    followerId,
                    event.entityType(),
                    event.entityId(),
                    event.message()
            );

            Notification notification = NotificationMapper.mapToNotification(followerNotification);

            notificationService.createNotification(notification);
        }
    }
}