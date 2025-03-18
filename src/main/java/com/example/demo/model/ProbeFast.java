package com.example.demo.model;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_repo.GridRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Slf4j
public class ProbeFast implements IOperationalProbe {

  private final Probe probe;
  private final GridRepo gridRepo;

  public ProbeFast(Probe probe, GridRepo gridRepo) {
    this.probe = probe;
    this.gridRepo = gridRepo;
  }

  @Override
  public Probe getProbe() {
    return probe;
  }

  @Override
  public void updateGridFromDatabase() {
    Grid grid = this.probe.getGrid();
    Grid gridUpdated =
        gridRepo
            .findById(grid.getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Grid with ID " + grid.getId() + " not found during update"));
    probe.setGrid(gridUpdated);
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
    this.probe.setXCoordinate(position.getX());
    this.probe.setYCoordinate(position.getY());
  }

  private Position calculateNewPosition(int step) {
    Position newPosition;
    final Position currentPosition = probe.getPosition();
    switch (probe.getDirection()) {
      case NORTH -> newPosition = currentPosition.calculateYPlus(step);
      case SOUTH -> newPosition = currentPosition.calculateYMinus(step);
      case EAST -> newPosition = currentPosition.calculateXPlus(step);
      case WEST -> newPosition = currentPosition.calculateXMinus(step);
      default ->
          throw new IllegalArgumentException(
              "Unexpected direction in checkMove: " + probe.getDirection());
    }
    boolean isValid = probe.getGrid().isValid(newPosition);
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
    Direction currentDirection = probe.getDirection();
    probe.setDirection(currentDirection.left());
  }

  @Override
  public void turnRight() {
    Direction currentDirection = probe.getDirection();
    probe.setDirection(currentDirection.right());
  }
}
