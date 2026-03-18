package com.unavu.notification.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListNotificationHandler extends BaseNotificationHandler {


    public ListNotificationHandler(NotificationService notificationService, SocialGraphFeignClient socialGraphFeignClient) {
        super(notificationService, socialGraphFeignClient);
    }

    public void handleListCreatedNotificationEvent(NotificationDto event) {
        log.info("List created notification event {}", event);
        fanOutToFollowers(event);
    }

    public void handleListItemCreatedNotificationEvent(NotificationDto event) {
        log.info("List item added notification event {}", event);
        fanOutToFollowers(event);
    }
}