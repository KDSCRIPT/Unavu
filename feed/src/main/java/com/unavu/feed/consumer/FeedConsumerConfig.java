package com.unavu.feed.consumer;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.handler.ListFeedHandler;
import com.unavu.feed.handler.RestaurantFeedHandler;
import com.unavu.feed.handler.ReviewFeedHandler;
import com.unavu.feed.handler.SocialGraphFeedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class FeedConsumerConfig {
    private final RestaurantFeedHandler restaurantHandler;
    private final SocialGraphFeedHandler socialHandler;
    private final ReviewFeedHandler reviewHandler;
    private final ListFeedHandler listHandler;

    @Bean
    public Consumer<FeedDto> feedConsumer() {

        return event -> {
            System.out.println("Received FeedDto event: " + event);
            switch (event.feedType()) {

                case RESTAURANT_CREATED ->
                        restaurantHandler.handleRestaurantCreatedFeedEvent(event);

                case RESTURANT_UPDATED ->
                        restaurantHandler.handleRestaurantUpdatedFeedEvent(event);

                case RESTAURANT_FOLLOWED ->
                        restaurantHandler.handleRestaurantFollowedFeedEvent(event);

                case USER_FOLLOWED ->
                        socialHandler.handleUserFollowedFeedEvent(event);

                case REVIEW_CREATED ->
                        reviewHandler.handleReviewCreatedFeedEvent(event);

                case LIST_CREATED ->
                        listHandler.handleListCreatedFeedEvent(event);

                case LIST_ITEM_ADDED ->
                        listHandler.handleListItemCreatedFeedEvent(event);
            }

        };
    }
}
