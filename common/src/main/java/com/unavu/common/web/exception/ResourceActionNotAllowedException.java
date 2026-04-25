package com.unavu.common.web.exception;

import org.springframework.http.HttpStatus;

public class ResourceActionNotAllowedException extends UnavuException {

    public ResourceActionNotAllowedException(String message) {
        super(message, HttpStatus.FORBIDDEN,message);
    }
}
