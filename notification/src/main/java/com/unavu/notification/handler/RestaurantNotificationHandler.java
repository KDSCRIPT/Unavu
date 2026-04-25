package com.unavu.notification.handler;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.service.NotificationService;
import com.unavu.notification.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantNotificationHandler extends BaseNotificationHandler {


    public RestaurantNotificationHandler(NotificationService notificationService, SocialGraphFeignClient socialGraphFeignClient) {
        super(notificationService, socialGraphFeignClient);
    }

    public void handleRestaurantCreatedNotificationEvent(NotificationDto event) {
        log.info("Restaurant created notification event {}", event);
        fanOutToFollowers(event);
    }

    public void handleRestaurantUpdatedNotificationEvent(NotificationDto event) {
        log.info("Restaurant updated notification event {}", event);
        fanOutToFollowers(event);
    }

    public void handleRestaurantFollowedNotificationEvent(NotificationDto event) {
        log.info("Restaurant followed notification event {}", event);
        fanOutToFollowers(event);
    }
}