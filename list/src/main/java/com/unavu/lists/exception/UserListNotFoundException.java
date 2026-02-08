package com.unavu.lists.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserListNotFoundException extends RuntimeException {
    public UserListNotFoundException(String fieldName, String fieldValue)
    {
        super(String.format("UserList not found with the given input data %s : '%s'",fieldName, fieldValue));
    }
}
