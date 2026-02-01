package com.unavu.users.exception;

public class DisplayNameAlreadyExistsException extends RuntimeException {
    public DisplayNameAlreadyExistsException(String message) {
        super(message);
    }
}
