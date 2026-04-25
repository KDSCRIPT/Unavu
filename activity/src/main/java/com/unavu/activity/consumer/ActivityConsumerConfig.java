package com.unavu.activity.consumer;

import com.unavu.activity.handler.ListActivityHandler;
import com.unavu.activity.handler.RestaurantActivityHandler;
import com.unavu.activity.handler.ReviewActivityHandler;
import com.unavu.activity.handler.SocialGraphActivityHandler;
import com.unavu.common.web.dto.ActivityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class ActivityConsumerConfig {
    private final SocialGraphActivityHandler socialHandler;
    private final ReviewActivityHandler reviewHandler;
    private final ListActivityHandler listHandler;
    private final RestaurantActivityHandler restaurantHandler;

    @Bean
    public Consumer<ActivityDto> activityConsumer() {

        return event -> {
            System.out.println("Received ActivityDto event: " + event);
            switch (event.activityType()) {

                case RESTAURANT_FOLLOWED->restaurantHandler.handleRestaurantFollowedActivityEvent(event);
                case RESTAURANT_UNFOLLOWED -> restaurantHandler.handleRestaurantUnFollowedActivityEvent(event);

                case USER_FOLLOWED->socialHandler.handleUserFollowedActivityEvent(event);
                case USER_UNFOLLOWED->socialHandler.handleUserUnFollowedActivityEvent(event);

                case REVIEW_CREATED->reviewHandler.handleReviewCreatedActivityEvent(event);
                case REVIEW_UPDATED->reviewHandler.handleReviewUpdatedActivityEvent(event);
                case REVIEW_DELETED->reviewHandler.handleReviewDeletedActivityEvent(event);

                case LIST_CREATED->listHandler.handleListCreatedActivityEvent(event);
                case LIST_UPDATED->listHandler.handleListUpdatedActivityEvent(event);
                case LIST_DELETED->listHandler.handleListDeletedActivityEvent(event);
                case LIST_ITEM_ADDED->listHandler.handleListItemCreatedActivityEvent(event);
                case LIST_ITEM_REMOVED->listHandler.handleListItemDeletedActivityEvent(event);
            }

        };
    }
}
