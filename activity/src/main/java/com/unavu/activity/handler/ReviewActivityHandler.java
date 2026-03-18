package com.unavu.activity.handler;

import com.unavu.activity.service.ActivityService;
import com.unavu.common.web.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReviewActivityHandler extends BaseActivityHandler {

    public ReviewActivityHandler(ActivityService activityService) {
        super(activityService);
    }

    public void handleReviewCreatedActivityEvent(ActivityDto event) {
        log.info("Review created activity event {}", event);
        processActivityEvent(event);
    }

    public void handleReviewUpdatedActivityEvent(ActivityDto event) {
        log.info("Review updated activity event {}", event);
        processActivityEvent(event);
    }

    public void handleReviewDeletedActivityEvent(ActivityDto event) {
        log.info("Review Deleted activity event {}", event);
        processActivityEvent(event);
    }
}