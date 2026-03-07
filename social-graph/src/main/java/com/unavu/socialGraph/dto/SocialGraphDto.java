package com.unavu.socialGraph.dto;

import com.unavu.socialGraph.entity.RelationshipType;
import lombok.Data;

@Data
public class SocialGraphDto {
    private Long id;
    private String fromUserId;
    private String toUserId;
    private RelationshipType relationshipType;
}

