package com.example.demo.model;

import com.example.demo.db_entity.Probe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class ProbeSlow implements IOperationalProbe {

  @Value("${probeSlow.moveTimeSec: 1}")
  private int moveTimeSec;

  private IOperationalProbe probeFast;

  public ProbeSlow(ProbeFast probeFast) {
    this.probeFast = probeFast;
  }

  @Override
  public Probe getProbe() {
    return probeFast.getProbe();
  }

  @Override
  public void updateGridFromDatabase() {
    probeFast.updateGridFromDatabase();
  }

  private void waitForMove() {
    try {
      Thread.sleep(moveTimeSec * 1000L);
    } catch (InterruptedException e) {
      log.error("Thread interrupted during wait: ", e);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public boolean moveForward() {
    waitForMove();
    return probeFast.moveForward();
  }

  @Override
  public void turnRight() {
    waitForMove();
    probeFast.turnRight();
  }

  @Override
  public void turnLeft() {
    waitForMove();
    probeFast.turnLeft();
  }

  @Override
  public boolean moveBackward() {
    waitForMove();
    return probeFast.moveBackward();
  }
}
