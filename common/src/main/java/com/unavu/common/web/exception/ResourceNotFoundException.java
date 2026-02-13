package com.unavu.common.web.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends UnavuException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {

        super(resourceName + " not found with " + fieldName + " : '" + fieldValue + "'",HttpStatus.NOT_FOUND,String.valueOf(fieldValue));
    }
}