package com.unavu.socialGraph.entity;

import com.unavu.common.core.BaseEntity;
import com.unavu.common.web.enums.EntityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "social_graph",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"actor_id","target_id","target_type","relationship_type"}
        ),
        indexes = {
                @Index(name = "idx_actor", columnList = "actor_id"),
                @Index(name = "idx_target", columnList = "target_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialGraph extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="actor_id",nullable = false,length = 36)
    private String actorId;

    @Column(name = "target_id", nullable = false)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private EntityType targetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
}
