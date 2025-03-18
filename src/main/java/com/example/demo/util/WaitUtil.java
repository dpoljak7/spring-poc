package com.example.demo.util;

import com.example.demo.exception.WaitException;
import org.springframework.stereotype.Service;

@Service
public class WaitUtil {

  public void wait(int secondsCount) {
    try {
      Thread.sleep(secondsCount * 1000L); // Convert seconds to milliseconds
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new WaitException("Thread was interrupted during the wait", e);
    }
  }
}
