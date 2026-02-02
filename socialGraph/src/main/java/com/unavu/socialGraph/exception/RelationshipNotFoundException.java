package com.unavu.socialGraph.exception;

import com.unavu.socialGraph.entity.RelationshipType;

public class RelationshipNotFoundException extends RuntimeException {

    public RelationshipNotFoundException(
            Long fromUserId,
            Long toUserId,
            RelationshipType relationshipType
    ) {
        super("Relationship not found: " + relationshipType.toString() +
                " from user " + fromUserId + " to user " + toUserId);
    }
}
