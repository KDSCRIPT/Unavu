package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.entity.Notification;
import com.unavu.notification.mapper.NotificationMapper;
import com.unavu.notification.service.NotificationDispatcher;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListNotificationHandler {


    private final NotificationDispatcher dispatcher;
    private final NotificationService notificationService;
    private final SocialGraphFeignClient socialGraphFeignClient;


    public void handleListCreatedEvent(NotificationDto event) {

        log.info("List created event {}", event);

        ResponseEntity<List<String>> response = socialGraphFeignClient.findFollowerIds(event.actorId());
        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            log.info("No followers found for user {} to notify list creation", event.actorId());
            return;
        }

        List<String> followerIds = response.getBody();


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

    public void handleListItemCreatedEvent(NotificationDto event) {

        log.info("List item added event {}", event);

        ResponseEntity<List<String>> response = socialGraphFeignClient.findFollowerIds(event.actorId());
        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            log.info("No followers found for user {} to notify list item addition", event.actorId());
            return;
        }

        List<String> followerIds = response.getBody();

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