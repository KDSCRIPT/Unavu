package com.unavu.common.web.dto;


import com.unavu.common.web.enums.EntityType;
import com.unavu.common.web.enums.NotificationType;

public record NotificationDto(
        NotificationType notificationType,
        String actorId,
        String targetUserId,
        EntityType entityType,
        Long entityId,
        String message
) {}