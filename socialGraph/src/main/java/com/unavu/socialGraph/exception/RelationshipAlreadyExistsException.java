package com.unavu.socialGraph.exception;

import com.unavu.socialGraph.entity.RelationshipType;

public class RelationshipAlreadyExistsException extends RuntimeException {

    public RelationshipAlreadyExistsException(
            Long fromUserId,
            Long toUserId,
            RelationshipType relationshipType
    ) {
        super("Relationship already exists: " + relationshipType.toString() +
                " from user " + fromUserId + " to user " + toUserId);
    }
}