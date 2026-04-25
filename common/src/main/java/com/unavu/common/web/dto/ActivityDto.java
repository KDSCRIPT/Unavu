package com.unavu.common.web.dto;


import com.unavu.common.web.enums.ActivityType;
import com.unavu.common.web.enums.EntityType;

public record ActivityDto(
        String userId,

        ActivityType activityType,

        EntityType entityType,

        Long entityId,

        String message
) {}