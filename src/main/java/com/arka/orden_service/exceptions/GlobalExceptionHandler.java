package com.arka.orden_service.exceptions;

import com.arka.orden_service.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler({OrdenNotFoundException.class,
        EmailNotFoundException.class,
        DireccionNotFoundException.class})
        public ResponseEntity<ErrorResponseDto> notFoundExceptions(
                Exception ex,
                WebRequest request) {

            ErrorResponseDto error = new ErrorResponseDto(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    ex.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(OrdenInvalidStateException.class)
        public ResponseEntity<ErrorResponseDto> handleOrdenInvalidState(
                OrdenInvalidStateException ex,
                WebRequest request) {

            ErrorResponseDto error = new ErrorResponseDto(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }


        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
                IllegalArgumentException ex,
                WebRequest request) {

            ErrorResponseDto error = new ErrorResponseDto(
                    LocalDateTime.now(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    request.getDescription(false).replace("uri=", "")
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDto> handleGlobalException(
                Exception ex,
                WebRequest request) {

            ErrorResponseDto error = new ErrorResponseDto(
                    LocalDateTime.now(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred. Please try again later.",
                    request.getDescription(false).replace("uri=", "")
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
}
