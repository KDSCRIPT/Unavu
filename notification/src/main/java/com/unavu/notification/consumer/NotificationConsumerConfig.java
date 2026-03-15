package com.unavu.notification.consumer;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.handler.ListNotificationHandler;
import com.unavu.notification.handler.RestaurantNotificationHandler;
import com.unavu.notification.handler.ReviewNotificationHandler;
import com.unavu.notification.handler.SocialGraphNotificationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class NotificationConsumerConfig {

    private final RestaurantNotificationHandler restaurantHandler;
    private final SocialGraphNotificationHandler socialHandler;
    private final ReviewNotificationHandler reviewHandler;
    private final ListNotificationHandler listHandler;

    @Bean
    public Consumer<NotificationDto> notificationConsumer() {

        return event -> {
            System.out.println("🔥 Received NotificationDto event: " + event);
            switch (event.notificationType()) {

                case RESTAURANT_CREATED ->
                        restaurantHandler.handleRestaurantCreatedEvent(event);

                case USER_FOLLOWED ->
                        socialHandler.handleUserFollowedEvent(event);

                case REVIEW_CREATED ->
                        reviewHandler.handleReviewCreatedEvent(event);

                case LIST_CREATED ->
                        listHandler.handleListCreatedEvent(event);

                case LIST_ITEM_ADDED ->
                        listHandler.handleListItemCreatedEvent(event);
            }

        };
    }
}