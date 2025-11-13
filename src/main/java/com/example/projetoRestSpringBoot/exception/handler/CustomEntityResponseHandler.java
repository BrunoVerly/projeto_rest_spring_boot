package com.example.projetoRestSpringBoot.exception.handler;

import com.example.projetoRestSpringBoot.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class CustomEntityResponseHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleResourceNotFound(
            ResourceNotFoundException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(RequiredObjectIsNullException.class)
    public final ResponseEntity<ExceptionResponse> handleRequiredObjectIsNull(
            RequiredObjectIsNullException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleBadRequest(
            BadRequestException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                e.getMessage(), webRequest);
    }
    @ExceptionHandler(EmailSendingException.class)
    public final ResponseEntity<ExceptionResponse> handleEmailSendingException(
            EmailSendingException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Email Sending Error",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthentication(
            InvalidJwtAuthenticationException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Forbidden",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ExceptionResponse> handleAuthenticationException(
            AuthenticationException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Credenciais inválidas", webRequest);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleFileNotFound(
            FileNotFoundException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "File Not Found",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(FileStorageException.class)
    public final ResponseEntity<ExceptionResponse> handleFileStorage(
            FileStorageException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File Storage Error",
                e.getMessage(), webRequest);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large",
                "Arquivo muito grande. Tamanho máximo permitido: 10MB", webRequest);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public final ResponseEntity<ExceptionResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Data Integrity Violation",
                "Erro ao processar dados: registro duplicado ou dados inválidos", webRequest);
    }

    @ExceptionHandler(java.io.IOException.class)
    public final ResponseEntity<ExceptionResponse> handleIOException(
            java.io.IOException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "IO Error",
                "Erro ao processar arquivo: " + e.getMessage(), webRequest);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ExceptionResponse> handleIllegalArgument(
            IllegalArgumentException e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                "Argumento inválido: " + e.getMessage(), webRequest);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ExceptionResponse> handleRuntimeException(
            RuntimeException e, WebRequest webRequest) {
        if (e.getMessage() != null && e.getMessage().contains("PagedResourcesAssembler")) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Configuration Error",
                    "Erro de configuração: PagedResourcesAssembler não inicializado", webRequest);
        }
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                e.getMessage() != null ? e.getMessage() : "Erro inesperado", webRequest);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(
            Exception e, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                e.getMessage(), webRequest);
    }

    private ResponseEntity<ExceptionResponse> buildErrorResponse(
            HttpStatus status, String error, String message, WebRequest webRequest) {
        ExceptionResponse response = new ExceptionResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                webRequest.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, status);
    }
}
