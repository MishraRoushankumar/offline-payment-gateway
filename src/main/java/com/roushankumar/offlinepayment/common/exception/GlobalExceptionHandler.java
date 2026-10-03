package com.roushankumar.offlinepayment.common.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exception) {
    ApiError error = new ApiError(
        HttpStatus.BAD_REQUEST.value(),
        exception.getMessage(),
        OffsetDateTime.now());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(error);
  }
}