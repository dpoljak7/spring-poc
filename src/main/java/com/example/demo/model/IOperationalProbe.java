package com.example.demo.model;

import com.example.demo.db_entity.Grid;

public interface IOperationalProbe {

  int getProbeId();

  void turnRight();

  void turnLeft();

  boolean moveBackward();

  /** Grid can be updated while Probe is operational and moving (e.g. new obstacles) */
  void updateGrid(Grid grid);

  Position getCurrentPosition();

  Direction getCurrentDirection();

  boolean moveForward();
}
