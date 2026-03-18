package com.unavu.socialGraph.repository;

import com.unavu.common.web.enums.EntityType;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SocialGraphRepository extends JpaRepository<SocialGraph, Long>,
        JpaSpecificationExecutor<SocialGraph> {

    Page<SocialGraph> findByActorId(String actorId, Pageable pageable);

    Page<SocialGraph> findByTargetIdAndTargetType(String targetId, EntityType targetType, Pageable pageable);

    boolean existsByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
            String actorId,
            String targetId,
            EntityType targetType,
            RelationshipType relationshipType
    );

    void deleteByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
            String actorId,
            String targetId,
            EntityType targetType,
            RelationshipType relationshipType
    );

    Optional<SocialGraph> findByActorIdAndTargetIdAndTargetTypeAndRelationshipType(
            String actorId,
            String targetId,
            EntityType targetType,
            RelationshipType relationshipType
    );

    Page<SocialGraph> findByActorIdAndRelationshipType(
            String actorId,
            RelationshipType relationshipType,
            Pageable pageable
    );

    Page<SocialGraph> findByTargetIdAndTargetTypeAndRelationshipType(
            String targetId,
            EntityType targetType,
            RelationshipType relationshipType,
            Pageable pageable
    );

    @Query("""
        SELECT sg.actorId
        FROM SocialGraph sg
        WHERE sg.targetId = :targetId
        AND sg.targetType = :targetType
        AND sg.relationshipType = :relationshipType
    """)
    List<String> findFollowerActorIds(
            @Param("targetId") String targetId,
            @Param("targetType") EntityType targetType,
            @Param("relationshipType") RelationshipType relationshipType
    );
}