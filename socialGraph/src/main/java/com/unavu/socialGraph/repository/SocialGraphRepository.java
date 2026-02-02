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
    Page<SocialGraph> findByFromUserId(Long fromUserId, Pageable pageable);

    Page<SocialGraph> findByToUserId(Long toUserId, Pageable pageable);

    boolean existsByFromUserIdAndToUserIdAndRelationshipType(Long fromUserId, Long toUserId, RelationshipType relationshipType);

    void deleteByFromUserIdAndToUserIdAndRelationshipType(Long fromUserId, Long toUserId, RelationshipType relationshipType);

    Optional<SocialGraph> findByFromUserIdAndToUserIdAndRelationshipType(Long fromUserId, Long toUserId, RelationshipType relationshipType);

    Page<SocialGraph> findByFromUserIdAndRelationshipType(Long fromUserId, RelationshipType relationshipType, Pageable pageable);

    Page<SocialGraph> findByToUserIdAndRelationshipType(Long toUserId, RelationshipType relationshipType, Pageable pageable);

}
