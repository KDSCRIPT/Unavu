package com.unavu.activity.handler;

import com.unavu.activity.service.ActivityService;
import com.unavu.common.web.dto.ActivityDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseActivityHandler {

    protected final ActivityService activityService;

    protected void processActivityEvent(ActivityDto event) {

        activityService.processActivityEvent(new ActivityDto(
                        event.userId(),
                        event.activityType(),
                        event.entityType(),
                        event.entityId(),
                        event.message()
                )
        );
    }
}