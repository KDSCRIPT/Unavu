package com.unavu.notification.entity;


import com.unavu.common.web.dto.EntityType;
import com.unavu.common.web.dto.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import com.unavu.common.core.BaseEntity;

@Entity
@Table(
        name="notifications",
        indexes={
                @Index(name="idx_notifications_user", columnList="user_id"),
                @Index(name="idx_notifications_user_created", columnList="user_id, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private String userId;

    @Column(name="actor_id")
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(name="notification_type", nullable=false)
    private NotificationType notificationType;

    @Column(name="entity_type")
    private EntityType entityType;

    @Column(name="entity_id")
    private Long entityId;

    private String message;

    @Column(name="is_read")
    private boolean isRead;

}