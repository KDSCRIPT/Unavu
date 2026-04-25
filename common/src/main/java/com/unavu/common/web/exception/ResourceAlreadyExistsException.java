package com.unavu.common.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends UnavuException {

    public ResourceAlreadyExistsException(String resource, String field, Object value) {

        super(resource + " already exists with " + field + " : '" + value + "'",HttpStatus.BAD_REQUEST,String.valueOf(value));
    }
}
