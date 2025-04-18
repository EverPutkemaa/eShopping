package com.online.eShopping.exception;

import com.online.eShopping.dto.ApiResponseDTO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Käitleb ResourceNotFoundException.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Käitleb InsufficientStockException.
     */
    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO> handleInsufficientStockException(InsufficientStockException ex) {
        log.error("Insufficient stock exception: {}", ex.getMessage());

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Käitleb DuplicateResourceException.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponseDTO> handleDuplicateResourceException(DuplicateResourceException ex) {
        log.error("Duplicate resource exception: {}", ex.getMessage());

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    /**
     * Käitleb InvalidOperationException.
     */
    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO> handleInvalidOperationException(InvalidOperationException ex) {
        log.error("Invalid operation exception: {}", ex.getMessage());

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Käitleb AccessDeniedException.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied exception: {}", ex.getMessage());

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message("You don't have permission to perform this action")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Käitleb valideerimise vigu.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message("Validation error")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Käitleb kõiki teisi erandeid.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponseDTO> handleGlobalException(Exception ex) {
        log.error("Unhandled exception: ", ex);

        ApiResponseDTO apiResponse = ApiResponseDTO.builder()
                .success(false)
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
