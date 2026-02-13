package com.unavu.common.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class UnavuException extends RuntimeException {

    @Getter
    private final HttpStatus status;
    private final String identifier;

    protected UnavuException(String message, HttpStatus status, String identifier) {
        super(message);
        this.status = status;
        this.identifier=identifier;
    }

}
