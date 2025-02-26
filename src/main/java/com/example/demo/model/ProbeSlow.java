package com.example.demo.model;

import com.example.demo.db_entity.Grid;
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

  private ProbeFast probeFast;

  public ProbeSlow(Grid grid, Position position, Direction direction) {
    probeFast = new ProbeFast(grid, position, direction);
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

  @Override
  public Position getCurrentPosition() {
    return probeFast.getCurrentPosition();
  }

  @Override
  public Direction getCurrentDirection() {
    return probeFast.getCurrentDirection();
  }
}
