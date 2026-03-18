package com.unavu.feed.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.service.FeedService;
import com.unavu.feed.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocialGraphFeedHandler extends BaseFeedHandler {

    public SocialGraphFeedHandler(FeedService feedService,
                                  SocialGraphFeignClient socialGraphFeignClient) {
        super(feedService, socialGraphFeignClient);
    }

    public void handleUserFollowedFeedEvent(FeedDto event) {
        log.info("User followed feed event {}", event);
        sendFeedToSelf(event);
    }
}