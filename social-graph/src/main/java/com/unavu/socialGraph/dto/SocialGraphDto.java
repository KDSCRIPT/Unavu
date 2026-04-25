package com.unavu.socialGraph.dto;

import com.unavu.common.web.enums.EntityType;
import com.unavu.socialGraph.entity.RelationshipType;
import lombok.Data;

@Data
public class SocialGraphDto {
    private Long id;
    private String actorId;
    private String targetId;
    private EntityType targetType;
    private RelationshipType relationshipType;
}

