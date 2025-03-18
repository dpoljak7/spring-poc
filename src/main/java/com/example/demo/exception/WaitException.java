package com.example.demo.exception;

public class WaitException extends RuntimeException {

  public WaitException() {
    super();
  }

  public WaitException(String message) {
    super(message);
  }

  public WaitException(String message, Throwable cause) {
    super(message, cause);
  }

  public WaitException(Throwable cause) {
    super(cause);
  }

  protected WaitException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
