package com.unavu.feed.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.service.FeedService;
import com.unavu.feed.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReviewFeedHandler extends BaseFeedHandler {

    public ReviewFeedHandler(FeedService feedService,
                             SocialGraphFeignClient socialGraphFeignClient) {
        super(feedService, socialGraphFeignClient);
    }

    public void handleReviewCreatedFeedEvent(FeedDto event) {
        log.info("Review created feed event {}", event);
        fanOutToFollowers(event);
    }
}