package com.unavu.activity.handler;

import com.unavu.activity.service.ActivityService;
import com.unavu.common.web.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocialGraphActivityHandler extends BaseActivityHandler {

    public SocialGraphActivityHandler(ActivityService activityService) {
        super(activityService);
    }

    public void handleUserFollowedActivityEvent(ActivityDto event) {
        log.info("User followed activity event {}", event);
        processActivityEvent(event);
    }

    public void handleUserUnFollowedActivityEvent(ActivityDto event) {
        log.info("User UnFollowed activity event {}", event);
        processActivityEvent(event);
    }
}