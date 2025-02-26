package com.example.demo.model;

import java.util.List;
import lombok.Data;

@Data
public class Grid {
  private final int xSize;
  private final int ySize;
  private final List<Position> obstacles;

  public boolean isValid(Position position) {
    if (position == null) {
      return false;
    }
    int x = position.getX();
    int y = position.getY();
    return x >= 0 && x < xSize && y >= 0 && y < ySize && !obstacles.contains(position);
  }
}
