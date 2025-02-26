package com.example.demo.model;

public interface IOperationalProbe {

  void turnRight();

  void turnLeft();

  boolean moveBackward();

  Position getCurrentPosition();

  Direction getCurrentDirection();

  boolean moveForward();
}
