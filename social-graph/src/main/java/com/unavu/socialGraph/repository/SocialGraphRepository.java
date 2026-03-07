package com.unavu.socialGraph.repository;

import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SocialGraphRepository extends JpaRepository<SocialGraph, Long>,
        JpaSpecificationExecutor<SocialGraph> {
    Page<SocialGraph> findByFromUserId(String fromUserId, Pageable pageable);

    Page<SocialGraph> findByToUserId(String toUserId, Pageable pageable);

    boolean existsByFromUserIdAndToUserIdAndRelationshipType(String fromUserId, String toUserId, RelationshipType relationshipType);

    void deleteByFromUserIdAndToUserIdAndRelationshipType(String fromUserId, String toUserId, RelationshipType relationshipType);

    Optional<SocialGraph> findByFromUserIdAndToUserIdAndRelationshipType(String fromUserId, String toUserId, RelationshipType relationshipType);

    Page<SocialGraph> findByFromUserIdAndRelationshipType(String fromUserId, RelationshipType relationshipType, Pageable pageable);

    Page<SocialGraph> findByToUserIdAndRelationshipType(String toUserId, RelationshipType relationshipType, Pageable pageable);

}
