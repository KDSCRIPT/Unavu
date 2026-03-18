package com.unavu.feed.handler;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.common.web.enums.EntityType;
import com.unavu.feed.service.FeedService;
import com.unavu.feed.service.client.SocialGraphFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseFeedHandler {

    protected final FeedService feedService;
    protected final SocialGraphFeignClient socialGraphFeignClient;

    protected void fanOutToFollowers(FeedDto event) {
        ResponseEntity<List<String>> response =
                socialGraphFeignClient.findFollowerIds(event.actorId(),EntityType.USER);

        if (response == null || response.getBody() == null || response.getBody().isEmpty()) {
            log.info("No followers for actorId={}", event.actorId());
            return;
        }

        response.getBody().forEach(followerId ->
                feedService.processFeedEvent(new FeedDto(
                        followerId,
                        event.actorId(),
                        event.feedType(),
                        event.entityType(),
                        event.entityId(),
                        event.message()
                ))
        );
    }
    protected void sendFeedToSelf(FeedDto event) {
                feedService.processFeedEvent(new FeedDto(
                        event.actorId(),
                        event.actorId(),
                        event.feedType(),
                        event.entityType(),
                        event.entityId(),
                        event.message()
                )
        );
    }
}