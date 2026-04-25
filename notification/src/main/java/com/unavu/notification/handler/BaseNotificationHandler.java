package com.unavu.notification.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.common.web.dto.NotificationDto;
import com.unavu.common.web.enums.EntityType;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseNotificationHandler {

    protected final NotificationService notificationService;
    protected final SocialGraphFeignClient socialGraphFeignClient;

    protected void fanOutToFollowers(NotificationDto event) {
        ResponseEntity<List<String>> response =
                socialGraphFeignClient.findFollowerIds(event.actorId(),EntityType.USER);

        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            log.info("No followers for actorId={}", event.actorId());
            return;
        }

        response.getBody().forEach(followerId ->
                notificationService.createNotification(new NotificationDto(
                        event.notificationType(),
                        followerId,
                        event.actorId(),
                        event.entityType(),
                        event.entityId(),
                        event.message()
                ))
        );
    }

    protected void sendNotificationToSelf(NotificationDto event) {
        notificationService.createNotification(new NotificationDto(
                        event.notificationType(),
                        event.targetUserId(),
                        event.targetUserId(),
                        event.entityType(),
                        event.entityId(),
                        event.message()
                )
        );
    }
}