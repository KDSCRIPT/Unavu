package com.unavu.activity.controller;

import com.unavu.activity.entity.Activity;
import com.unavu.common.provider.CurrentUserProvider;
import com.unavu.activity.service.ActivityService;
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
        name = "Activity REST APIs",
        description = "REST APIs to fetch user activity"
)
@Slf4j
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "Get my activity", description = "Returns paginated activity for the current user")
    @GetMapping("/activity/me")
    public ResponseEntity<Page<Activity>> getMyActivity(Pageable pageable) {
        String userId = currentUserProvider.getCurrentUserId();
        log.info("Fetching activity for userId={}", userId);
        return ResponseEntity.ok(activityService.getUserActivity(userId, pageable));
    }
}