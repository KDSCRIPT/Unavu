package com.unavu.feed.mapper;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.entity.Feed;

public class FeedMapper {


    public static Feed mapToFeed(FeedDto feedDto) {
        Feed feed = new Feed();
        feed.setUserId(feedDto.userId());
        feed.setActorId(feedDto.actorId());
        feed.setFeedType(feedDto.feedType());
        feed.setEntityId(feedDto.entityId());
        feed.setEntityType(feedDto.entityType());
        feed.setMessage(feedDto.message());
        return feed;
    }
}
