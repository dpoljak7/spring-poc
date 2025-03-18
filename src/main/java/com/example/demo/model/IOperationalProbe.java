package com.example.demo.model;

import com.example.demo.db_entity.Probe;

public interface IOperationalProbe {

  /** Contains Probe data initially retrieved from database. */
  Probe getProbe();

  void turnRight();

  void turnLeft();

  boolean moveBackward();

  /** Grid can be updated while Probe is operational and moving (e.g. new obstacles) */
  void updateGridFromDatabase();

  boolean moveForward();
}
