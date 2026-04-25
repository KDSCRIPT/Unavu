package com.unavu.activity.handler;

import com.unavu.activity.service.ActivityService;
import com.unavu.common.web.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantActivityHandler extends BaseActivityHandler  {

    public RestaurantActivityHandler(ActivityService activityService) {
        super(activityService);
    }

    public void handleRestaurantFollowedActivityEvent(ActivityDto event) {
        log.info("Restaurant followed activity event {}", event);
        processActivityEvent(event);
    }

    public void handleRestaurantUnFollowedActivityEvent(ActivityDto event) {
        log.info("Restaurant Unfollowed activity event {}", event);
        processActivityEvent(event);
    }
}
