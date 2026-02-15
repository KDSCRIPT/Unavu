package com.unavu.socialGraph.entity;

import com.unavu.common.core.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "social_graph",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"from_user_id","to_user_id","relationship_type"}
        ),
        indexes = {
                @Index(name = "idx_from_user", columnList = "from_user_id"),
                @Index(name = "idx_to_user", columnList = "to_user_id")
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

    @Column(name="from_user_id",nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
}
