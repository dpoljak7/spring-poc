package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Position {
  private int x;
  private int y;

  public Position calculateXPlus(int step) {
    return new Position(this.x + step, this.y);
  }

  public Position calculateXMinus(int step) {
    return new Position(this.x - step, this.y);
  }

  public Position calculateYPlus(int step) {
    return new Position(this.x, this.y + step);
  }

  public Position calculateYMinus(int step) {
    return new Position(this.x, this.y - step);
  }
}
