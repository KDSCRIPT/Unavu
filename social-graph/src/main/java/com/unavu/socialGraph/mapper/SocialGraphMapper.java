package com.unavu.socialGraph.mapper;

import com.unavu.common.web.enums.EntityType;
import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;

public class SocialGraphMapper {

    public static SocialGraph toEntity(
            String actorId,
            String targetId,
            EntityType targetType,
            RelationshipType relationshipType
    ) {
        SocialGraph socialGraph = new SocialGraph();
        socialGraph.setActorId(actorId);
        socialGraph.setTargetId(targetId);
        socialGraph.setTargetType(targetType);
        socialGraph.setRelationshipType(relationshipType);
        return socialGraph;
    }

    public static SocialGraphDto toDto(SocialGraph socialGraph) {
        if (socialGraph == null) return null;

        SocialGraphDto dto = new SocialGraphDto();
        dto.setId(socialGraph.getId());
        dto.setActorId(socialGraph.getActorId());
        dto.setTargetId(socialGraph.getTargetId());
        dto.setTargetType(socialGraph.getTargetType());
        dto.setRelationshipType(socialGraph.getRelationshipType());

        return dto;
    }
}