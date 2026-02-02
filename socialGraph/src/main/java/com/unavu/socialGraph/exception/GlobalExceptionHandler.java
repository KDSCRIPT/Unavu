package com.unavu.socialGraph.exception;

import com.unavu.socialGraph.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)
    {
        Map<String,String> validationErrors=new HashMap<>();
        List<ObjectError> validationErrorList=ex.getBindingResult().getAllErrors();

        validationErrorList.forEach((error)->{
            String fieldName=((FieldError) error).getField();
            String validationMsg=error.getDefaultMessage();
            validationErrors.put(fieldName,validationMsg);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(RuntimeException runtimeException, WebRequest webRequest)
    {
        ErrorResponseDto errorResponseDto= new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                runtimeException.getMessage(),
                LocalDateTime.now()
        );
        log.error("Unhandled exception occurred", runtimeException);
        return new ResponseEntity<>(errorResponseDto,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RelationshipAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleRelationshipAlreadyExistsException(RelationshipAlreadyExistsException runtimeException, WebRequest webRequest)
    {
        ErrorResponseDto errorResponseDto= new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.CONFLICT,
                runtimeException.getMessage(),
                LocalDateTime.now()
        );
        log.error("Unhandled exception occurred", runtimeException);
        return new ResponseEntity<>(errorResponseDto,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RelationshipNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRelationshipNotFoundException(RelationshipNotFoundException exception,WebRequest webRequest)
    {
        ErrorResponseDto errorResponseDto=new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    public ResponseEntity<ErrorResponseDto> handleActionNotAllowedException(ActionNotAllowedException exception, WebRequest webRequest)
    {
        ErrorResponseDto errorResponseDto=new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto,HttpStatus.FORBIDDEN);
    }
}
