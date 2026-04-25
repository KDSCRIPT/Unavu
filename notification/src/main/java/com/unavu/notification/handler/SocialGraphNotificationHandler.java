package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocialGraphNotificationHandler extends BaseNotificationHandler {


    public SocialGraphNotificationHandler(NotificationService notificationService, SocialGraphFeignClient socialGraphFeignClient) {
        super(notificationService, socialGraphFeignClient);
    }

    public void handleUserFollowedNotificationEvent(NotificationDto event) {
        log.info("User followed notification event {}", event);
        sendNotificationToSelf(event);
    }
}