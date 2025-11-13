package com.example.projetoRestSpringBoot.exception;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

public record ExceptionResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) { }
