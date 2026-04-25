package com.unavu.feed.entity;

import com.unavu.common.core.BaseEntity;
import com.unavu.common.web.enums.EntityType;
import com.unavu.common.web.enums.FeedType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_feed",
        indexes = {
                @Index(name = "idx_feed_user", columnList = "user_id"),
                @Index(name = "idx_feed_created", columnList = "created_at"),
                @Index(name = "idx_feed_user_created", columnList = "user_id, created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "actor_id", nullable = false)
    private String actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feed_type", nullable = false)
    private FeedType feedType;

    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    private String message;

}