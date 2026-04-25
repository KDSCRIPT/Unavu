package com.unavu.feed.service;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedService {

    void processFeedEvent(FeedDto dto);

    Page<Feed> getUserFeed(String userId, Pageable pageable);


}