package com.unavu.socialGraph.dto;

import com.unavu.socialGraph.entity.RelationshipType;
import lombok.Data;

@Data
public class SocialGraphDto {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private RelationshipType relationshipType;
}

