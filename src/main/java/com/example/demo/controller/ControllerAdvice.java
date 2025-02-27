package com.example.demo.controller;

import com.example.demo.exception.AutopilotNoPathException;
import com.example.demo.exception.CommandException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    log.error("An exception occurred: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred. Please try again later. Exception: " + ex.getMessage());
  }

  @ExceptionHandler({CommandException.class, AutopilotNoPathException.class})
  public ResponseEntity<String> handleCommandException(Exception ex) {
    log.error("An exception occurred: ", ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("An unexpected error occurred. Please try again later. Exception=" + ex.getMessage());
  }
}
