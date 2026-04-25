package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReviewNotificationHandler extends BaseNotificationHandler {


    public ReviewNotificationHandler(NotificationService notificationService, SocialGraphFeignClient socialGraphFeignClient) {
        super(notificationService, socialGraphFeignClient);
    }

    public void handleReviewCreatedNotificationEvent(NotificationDto event) {
        log.info("Review created notification event {}", event);
        fanOutToFollowers(event);
    }
}