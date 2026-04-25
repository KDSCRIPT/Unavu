package com.unavu.activity.mapper;

import com.unavu.activity.entity.Activity;
import com.unavu.common.web.dto.ActivityDto;

public class ActivityMapper {


    public static Activity mapToActivity(ActivityDto activityDto) {
        Activity activity = new Activity();
        activity.setUserId(activityDto.userId());
        activity.setActivityType(activityDto.activityType());
        activity.setEntityId(activityDto.entityId());
        activity.setEntityType(activityDto.entityType());
        activity.setMessage(activityDto.message());
        return activity;
    }
}
