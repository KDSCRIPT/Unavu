package com.unavu.users.service.impl;

import com.unavu.users.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> displayNameContains(String displayName) {
        return (root, query, cb) ->
                displayName == null
                        ? cb.conjunction()
                        : cb.like(
                        cb.lower(root.get("displayName")),
                        "%" + displayName.toLowerCase() + "%"
                );
    }
}
