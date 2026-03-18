package com.unavu.common.web.dto;


import com.unavu.common.web.enums.EntityType;
import com.unavu.common.web.enums.FeedType;

public record FeedDto(
        String userId,

        String actorId,

        FeedType feedType,

        EntityType entityType,

        Long entityId,

        String message
) {}