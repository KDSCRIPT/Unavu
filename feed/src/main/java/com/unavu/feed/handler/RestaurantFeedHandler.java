package com.unavu.feed.handler;


import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.service.FeedService;
import com.unavu.feed.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantFeedHandler extends BaseFeedHandler {

    public RestaurantFeedHandler(FeedService feedService,
                                 SocialGraphFeignClient socialGraphFeignClient) {
        super(feedService, socialGraphFeignClient);
    }

    public void handleRestaurantCreatedFeedEvent(FeedDto event) {
        log.info("Restaurant created feed event {}", event);
        fanOutToFollowers(event);
    }

    public void handleRestaurantUpdatedFeedEvent(FeedDto event) {
        log.info("Restaurant updated feed event {}", event);
        fanOutToFollowers(event);
    }

    public void handleRestaurantFollowedFeedEvent(FeedDto event) {
        log.info("Restaurant followed feed event {}", event);
        fanOutToFollowers(event);
    }
}