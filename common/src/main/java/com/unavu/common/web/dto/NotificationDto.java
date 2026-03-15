package com.unavu.common.web.dto;



public record NotificationDto(
        NotificationType notificationType,
        String actorId,
        String targetUserId,
        EntityType entityType,
        Long entityId,
        String message
) {}