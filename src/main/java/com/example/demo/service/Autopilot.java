package com.example.demo.service;

import com.example.demo.db_entity.Grid;
import com.example.demo.model.Direction;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.model.Position;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Autopilot {

  /**
   *
   *
   * <pre>
   * Command returned is in form FBRL where:
   * F - forward
   * B - backwards
   * R - right
   * L - left
   * Example: FFRFF
   * </pre>
   */
  public String createCommandsForDestination(
    IOperationalProbe operationalProbe, Position destination) {

    Grid grid = operationalProbe.getProbe().getGrid();
    Position start = operationalProbe.getProbe().getPosition();
    Direction direction = operationalProbe.getProbe().getDirection();
    // First: BFS to find a valid path from start to destination.
    Queue<Position> queue = new LinkedList<>();
    Map<Position, Position> cameFrom = new HashMap<>();
    Set<Position> visited = new HashSet<>();

    queue.add(start);
    visited.add(start);

    boolean found = false;
    found = findPathUsingBFSAlgorithm(grid, destination, queue, found, visited, cameFrom);

    if (!found) {
      log.info("No path found for journey from start={} to destination={}", start, destination);
      return null;
    }

    StringBuilder commands = reconstructPathIntoCommand(start, direction, destination, cameFrom);

    return commands.toString();
  }

  /**
   *
   *
   * <pre>
   * Command returned is in form FBRL where:
   * F - forward
   * B - backwards
   * R - right
   * L - left
   * </pre>
   */
  private StringBuilder reconstructPathIntoCommand(
      Position start, Direction direction, Position destination, Map<Position, Position> cameFrom) {
    // Reconstruct the path from destination to start.
    LinkedList<Position> path = new LinkedList<>();
    Position step = destination;
    while (!step.equals(start)) {
      path.addFirst(step);
      step = cameFrom.get(step);
    }
    // 'path' now contains the sequence of positions to move through (excluding the start).

    // Second: Convert the path to the string commands.
    StringBuilder commands = new StringBuilder();
    Direction currentDir = direction;
    Position currentPos = start;

    for (Position nextPos : path) {
      int dx = nextPos.getX() - currentPos.getX();
      int dy = nextPos.getY() - currentPos.getY();

      // Determine the desired direction for this step.
      Direction desiredDir = null;
      if (dx == 1 && dy == 0) {
        desiredDir = Direction.EAST;
      } else if (dx == -1 && dy == 0) {
        desiredDir = Direction.WEST;
      } else if (dy == 1 && dx == 0) {
        desiredDir = Direction.NORTH;
      } else if (dy == -1 && dx == 0) {
        desiredDir = Direction.SOUTH;
      } else {
        // This should not happen if neighbors are chosen correctly.
        throw new IllegalStateException("Invalid move from " + currentPos + " to " + nextPos);
      }

      // Determine the turning commands needed based on the current facing.
      if (currentDir == desiredDir) {
        // Already facing the right direction.
        commands.append("F");
      } else if (desiredDir == oppositeOf(currentDir)) {
        // Opposite direction: use the 'B' command to move backward.
        commands.append("B");
      } else if (currentDir.left() == desiredDir) {
        // Turn left then move forward.
        commands.append("L").append("F");
        currentDir = currentDir.left();
      } else if (currentDir.right() == desiredDir) {
        // Turn right then move forward.
        commands.append("R").append("F");
        currentDir = currentDir.right();
      } else {
        // Fallback (should not occur with simple cardinal moves): turn left twice.
        commands.append("L").append("L").append("F");
        currentDir = currentDir.left().left();
      }

      // Update the current position.
      currentPos = nextPos;
    }
    return commands;
  }

  private static boolean findPathUsingBFSAlgorithm(
      Grid grid,
      Position destination,
      Queue<Position> queue,
      boolean found,
      Set<Position> visited,
      Map<Position, Position> cameFrom) {
    while (!queue.isEmpty()) {
      Position current = queue.poll();
      if (current.equals(destination)) {
        found = true;
        break;
      }

      // Generate neighbors: north, south, east, west.
      List<Position> neighbors =
          Arrays.asList(
              current.calculateYPlus(1), // north (y + 1)
              current.calculateYMinus(1), // south (y - 1)
              current.calculateXPlus(1), // east  (x + 1)
              current.calculateXMinus(1) // west  (x - 1)
              );

      for (Position neighbor : neighbors) {
        if (grid.isValid(neighbor) && !visited.contains(neighbor)) {
          queue.add(neighbor);
          visited.add(neighbor);
          cameFrom.put(neighbor, current);
        }
      }
    }
    return found;
  }

  // Helper method to return the opposite direction.
  private Direction oppositeOf(Direction dir) {
    return switch (dir) {
      case NORTH -> Direction.SOUTH;
      case SOUTH -> Direction.NORTH;
      case EAST -> Direction.WEST;
      case WEST -> Direction.EAST;
    };
  }
}
