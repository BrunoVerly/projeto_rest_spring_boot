package com.example.projetoRestSpringBoot.exception.handler;

import com.example.projetoRestSpringBoot.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception e, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                e.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> ResourceNotFoundException(Exception e, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                e.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(RequiredObjectIsNullException.class)
        public final ResponseEntity<ExceptionResponse> RequiredObjectIsNullException(Exception e, WebRequest webRequest) {
            ExceptionResponse response = new ExceptionResponse(
                    new Date(),
                    e.getMessage(),
                    webRequest.getDescription(false));

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> FileNotFoundExceptions(Exception e, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                e.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(FileStorageException.class)
    public final ResponseEntity<ExceptionResponse> FileStorageExceptions (Exception e, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                e.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionResponse> BadRequestException(Exception e, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                e.getMessage(),
                webRequest.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

}
