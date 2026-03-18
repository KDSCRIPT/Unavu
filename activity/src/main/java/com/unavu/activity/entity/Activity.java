package com.unavu.activity.entity;

import com.unavu.common.core.BaseEntity;
import com.unavu.common.web.enums.ActivityType;
import com.unavu.common.web.enums.EntityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_activity",
        indexes = {
                @Index(name = "idx_activity_user", columnList = "user_id"),
                @Index(name = "idx_activity_created", columnList = "created_at"),
                @Index(name = "idx_activity_user_created", columnList = "user_id, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    private String message;

}