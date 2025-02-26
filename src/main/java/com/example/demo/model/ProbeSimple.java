package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@AllArgsConstructor
@Slf4j
public class ProbeSimple implements IProbe {

  private Grid grid;
  private Position position;
  private Direction direction;

  @Override
  public Position getCurrentPosition() {
    return position;
  }

  @Override
  public Direction getDirection() {
    return direction;
  }

  @Override
  public boolean moveForward() {
    move(1); // positive step to move forward
    boolean isValid = grid.isValid(position);
    if (!isValid) {
      log.debug(
          "Invalid position detected, position={}. Stepping back to the previous position.",
          position);
      move(-1);
    }
    return isValid;
  }

  @Override
  public boolean moveBackward() {
    move(-1); // negative step to move backward
    boolean isValid = grid.isValid(position);
    if (!isValid) {
      log.debug(
          "Invalid position detected, position={}. Stepping back to the previous position.",
          position);
      move(+1);
    }
    return isValid;
  }

  private void move(int step) {
    switch (direction) {
      case NORTH -> position.yPlus(step);
      case SOUTH -> position.yMinus(step);
      case EAST -> position.xPlus(step);
      case WEST -> position.xMinus(step);
    }
  }

  @Override
  public void turnLeft() {
    direction = direction.left();
  }

  @Override
  public void turnRight() {
    direction = direction.right();
  }
}
