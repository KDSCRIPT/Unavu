package com.unavu.socialGraph.mapper;

import com.unavu.socialGraph.dto.SocialGraphDto;
import com.unavu.socialGraph.entity.RelationshipType;
import com.unavu.socialGraph.entity.SocialGraph;

public class SocialGraphMapper {

    public static SocialGraph toEntity(
            Long fromUserId,
            Long toUserId,
            RelationshipType relationshipType
    ) {
        SocialGraph socialGraph = new SocialGraph();
        socialGraph.setFromUserId(fromUserId);
        socialGraph.setToUserId(toUserId);
        socialGraph.setRelationshipType(relationshipType);
        return socialGraph;
    }

    public static SocialGraphDto toDto(SocialGraph socialGraph) {
        if (socialGraph == null) return null;
        SocialGraphDto socialGraphDto = new SocialGraphDto();
        socialGraphDto.setId(socialGraph.getId());
        socialGraphDto.setFromUserId(socialGraph.getFromUserId());
        socialGraphDto.setToUserId(socialGraph.getToUserId());
        socialGraphDto.setRelationshipType(socialGraph.getRelationshipType());
        return socialGraphDto;
    }
}

