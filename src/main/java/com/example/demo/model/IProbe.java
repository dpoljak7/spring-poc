package com.example.demo.model;

public interface IProbe {

  void turnRight();

  void turnLeft();

  boolean moveBackward();

  Position getCurrentPosition();

  Direction getDirection();

  boolean moveForward();
}
