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
            switch (event.notificationType()) {

                case RESTAURANT_CREATED ->
                        restaurantHandler.handleRestaurantCreatedNotificationEvent(event);

                case RESTURANT_UPDATED ->
                        restaurantHandler.handleRestaurantUpdatedNotificationEvent(event);

                case RESTAURANT_FOLLOWED ->
                        restaurantHandler.handleRestaurantFollowedNotificationEvent(event);

                case USER_FOLLOWED ->
                        socialHandler.handleUserFollowedNotificationEvent(event);

                case REVIEW_CREATED ->
                        reviewHandler.handleReviewCreatedNotificationEvent(event);

                case LIST_CREATED ->
                        listHandler.handleListCreatedNotificationEvent(event);

                case LIST_ITEM_ADDED ->
                        listHandler.handleListItemCreatedNotificationEvent(event);
            }

        };
    }
}