package com.example.demo.model;

import com.example.demo.db_entity.Grid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class ProbeFast implements IOperationalProbe {

  private int probeId;
  private Grid grid;
  private Position position;
  private Direction direction;

  public ProbeFast(int probeId, Grid grid, Position position, Direction direction) {
    this.probeId = probeId;
    this.grid = grid;
    this.position = position;
    this.direction = direction;
  }

  @Override
  public int getProbeId() {
    return probeId;
  }

  @Override
  public void updateGrid(Grid grid) {
    this.grid = grid;
  }

  @Override
  public Position getCurrentPosition() {
    return position;
  }

  @Override
  public Direction getCurrentDirection() {
    return direction;
  }

  @Override
  public boolean moveForward() {
    Position newPosition = calculateNewPosition(1);
    if (newPosition == null) {
      return false;
    }
    move(newPosition);
    return true;
  }

  @Override
  public boolean moveBackward() {
    Position newPosition = calculateNewPosition(-1);
    if (newPosition == null) {
      return false;
    }
    move(newPosition);
    return true;
  }

  private void move(Position position) {
    this.position.setX(position.getX());
    this.position.setY(position.getY());
  }

  private Position calculateNewPosition(int step) {
    Position newPosition;
    switch (direction) {
      case NORTH -> newPosition = position.calculateYPlus(step);
      case SOUTH -> newPosition = position.calculateYMinus(step);
      case EAST -> newPosition = position.calculateXPlus(step);
      case WEST -> newPosition = position.calculateXMinus(step);
      default ->
          throw new IllegalArgumentException("Unexpected direction in checkMove: " + direction);
    }
    boolean isValid = grid.isValid(newPosition);
    if (!isValid) {
      log.debug(
          "Probe cannot follow command. Reasons: going outside of the grid or obstacle reached, new position="
              + newPosition);
      return null;
    }
    return newPosition;
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
