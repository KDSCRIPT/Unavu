package com.unavu.feed.service.impl;

import com.unavu.common.web.dto.FeedDto;
import com.unavu.feed.entity.Feed;
import com.unavu.feed.mapper.FeedMapper;
import com.unavu.feed.repository.FeedRepository;
import com.unavu.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;

    @Override
    public void processFeedEvent(FeedDto dto) {

        Feed feed = FeedMapper.mapToFeed(dto);
        feedRepository.save(feed);
    }

    @Override
    public Page<Feed> getUserFeed(String userId, Pageable pageable) {
        return feedRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
