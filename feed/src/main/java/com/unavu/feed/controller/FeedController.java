package com.unavu.feed.controller;

import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.feed.entity.Feed;
import com.unavu.feed.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Feed REST APIs",
        description = "REST APIs to fetch user feed"
)
@Slf4j
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Get my feed", description = "Returns paginated feed for the current user")
    @GetMapping("/feed/me")
    public ResponseEntity<Page<Feed>> getMyFeed(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching feed for userId={}", userId);
        return ResponseEntity.ok(feedService.getUserFeed(userId, pageable));
    }
}