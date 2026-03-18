package com.unavu.activity.handler;

import com.unavu.activity.service.ActivityService;
import com.unavu.common.web.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListActivityHandler extends BaseActivityHandler {

    public ListActivityHandler(ActivityService activityService) {
        super(activityService);
    }

    public void handleListCreatedActivityEvent(ActivityDto event) {
        log.info("List created activity event {}", event);
        processActivityEvent(event);
    }

    public void handleListItemCreatedActivityEvent(ActivityDto event) {
        log.info("List item added activity event {}", event);
        processActivityEvent(event);
    }

    public void handleListDeletedActivityEvent(ActivityDto event) {
        log.info("List deleted activity event {}", event);
        processActivityEvent(event);
    }

    public void handleListItemDeletedActivityEvent(ActivityDto event) {
        log.info("List item deleted activity event {}", event);
        processActivityEvent(event);
    }

    public void handleListUpdatedActivityEvent(ActivityDto event) {
        log.info("List updated activity event {}", event);
        processActivityEvent(event);
    }
}