package com.bicmamwservice.exception;


import com.bicmamwservice.model.ModelApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ModelApiResponse> responseNotFoundExceptionHandler(ResourceNotFoundException exception) {
        String message = exception.getMessage();
        return new ResponseEntity<>(new ModelApiResponse(HttpStatus.NOT_FOUND.value(), message), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ModelApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(new ModelApiResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()), HttpStatus.FORBIDDEN);
    }
}
