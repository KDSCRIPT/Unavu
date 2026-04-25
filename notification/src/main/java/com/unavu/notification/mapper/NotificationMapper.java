package com.unavu.notification.mapper;

import com.unavu.common.web.dto.NotificationDto;
import com.unavu.notification.entity.Notification;

public class NotificationMapper {


    public static Notification mapToNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setUserId(notificationDto.targetUserId());
        notification.setActorId(notificationDto.actorId());
        notification.setNotificationType(notificationDto.notificationType());
        notification.setEntityId(notificationDto.entityId());
        notification.setEntityType(notificationDto.entityType());
        notification.setMessage(notificationDto.message());
        notification.setRead(false);
        return notification;
    }
}
