package com.example.demo.exception;

public class AutopilotNoPathException extends RuntimeException {

  public AutopilotNoPathException() {
  }

  public AutopilotNoPathException(String message) {
    super(message);
  }

  public AutopilotNoPathException(String message, Throwable cause) {
    super(message, cause);
  }

  public AutopilotNoPathException(Throwable cause) {
    super(cause);
  }

}
