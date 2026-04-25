package com.unavu.feed.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.service.FeedService;
import com.unavu.feed.service.client.SocialGraphFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListFeedHandler extends BaseFeedHandler {

    public ListFeedHandler(FeedService feedService,
                           SocialGraphFeignClient socialGraphFeignClient) {
        super(feedService, socialGraphFeignClient);
    }

    public void handleListCreatedFeedEvent(FeedDto event) {
        log.info("List created feed event {}", event);
        fanOutToFollowers(event);
    }

    public void handleListItemCreatedFeedEvent(FeedDto event) {
        log.info("List item added feed event {}", event);
        fanOutToFollowers(event);
    }
}