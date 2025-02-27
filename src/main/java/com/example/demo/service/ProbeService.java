package com.example.demo.service;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.exception.CommandException;
import com.example.demo.model.Direction;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeFactory;
import com.example.demo.model.ProbeFast;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.util.WaitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProbeService {

  @Autowired private ProbeFactory probeFactory;

  @Autowired private ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  @Autowired private ProbeRepo probeRepo;

  @Autowired private GridRepo gridRepo;
  
  @Autowired private WaitUtil waitUtil;

  @Value("${probeMove.retriesCount}")
  private int probeMoveRetriesCount;

  @Value("${probeMove.retryDelaySec}")
  private int proveMoveRetryDelaySec;

  public int initialize(Grid grid, Position position, Direction direction) {
    Grid gridSaved = gridRepo.save(grid);
    Probe probe =
        Probe.builder()
            .probeType(ProbeFast.class.getSimpleName())
            .grid(gridSaved)
            .direction(direction)
            .build();
    Probe probeSaved = probeRepo.save(probe);

    ProbeVisitedPosition visitedPosition =
        ProbeVisitedPosition.builder()
            .probeId(probeSaved.getId())
            .username(SecurityContextHolder.getContext().getAuthentication().getName())
            .xCoordinate(position.getX())
            .yCoordinate(position.getY())
            .direction(direction)
            .commandExecuted("INIT")
            .timestampVisited(LocalDateTime.now())
            .build();
    probeVisitedPositionsRepo.save(visitedPosition);

    return probe.getId();
  }

  /**
   *
   *
   * <pre>
   * Method is implemented asynchronously because execution can be long if many commands are given
   * </pre>
   */
  //  @Async
  public void execute(int probeId, String command) {
    Probe probe =
        probeRepo
            .findById(probeId)
            .orElseThrow(
                () -> new IllegalArgumentException("Probe with ID " + probeId + " not found"));

    int gridId = probe.getGrid().getId();
    Grid grid =
        gridRepo
            .findById(gridId)
            .orElseThrow(() -> new IllegalStateException("Grid with ID " + gridId + " not found"));
    Position position = new Position(probe.getXCoordinate(), probe.getYCoordinate());
    IOperationalProbe probeImpl =
        probeFactory.createProbeFast(grid, position, probe.getDirection());

    for (char cmd : command.toUpperCase().toCharArray()) {
      boolean success = false;
      for (int retries = 0; retries < probeMoveRetriesCount; retries++) {
        success = executeMoveCommand(cmd, probeImpl);

        if (success) {
          break; // Exit retry loop if the move was successful
        } else {
          waitUtil.wait(proveMoveRetryDelaySec);
          updateProbeWithNewestGrid(grid, probeImpl);
        }
      }

      if (!success) {
        throw new CommandException(
          "Failed to execute command " + cmd + " after " + probeMoveRetriesCount + " retries with retryDelay=" + proveMoveRetryDelaySec);
      }

      updateProbeMoveData(probeId, cmd, probeImpl, probe);
    }
  }

  private void updateProbeMoveData(int probeId, char cmd, IOperationalProbe probeImpl, Probe probe) {
    ProbeVisitedPosition visitedPosition =
      ProbeVisitedPosition.builder()
                          .probeId(probeId)
                          .username(SecurityContextHolder.getContext().getAuthentication().getName())
                          .xCoordinate(probeImpl.getCurrentPosition().getX())
                          .yCoordinate(probeImpl.getCurrentPosition().getY())
                          .direction(probeImpl.getCurrentDirection())
                          .commandExecuted(String.valueOf(cmd))
                          .timestampVisited(LocalDateTime.now())
                          .build();
    probeVisitedPositionsRepo.save(visitedPosition);

    probe.setXCoordinate(probeImpl.getCurrentPosition().getX());
    probe.setYCoordinate(probeImpl.getCurrentPosition().getY());
    probe.setDirection(probeImpl.getCurrentDirection());
    probeRepo.save(probe);
  }

  private static boolean executeMoveCommand(char cmd, IOperationalProbe probeImpl) {
    boolean success;
    switch (cmd) {
      case 'F':
        success = probeImpl.moveForward();
        break;
      case 'B':
        success = probeImpl.moveBackward();
        break;
      case 'L':
        probeImpl.turnLeft();
        success = true; // No retries needed for turning
        break;
      case 'R':
        probeImpl.turnRight();
        success = true; // No retries needed for turning
        break;
      default:
        throw new IllegalArgumentException("Invalid command: " + cmd);
    }
    return success;
  }

  private void updateProbeWithNewestGrid(final Grid grid, IOperationalProbe probe) {
    Grid gridUpdated = gridRepo
      .findById(grid.getId())
      .orElseThrow(() -> new IllegalStateException("Grid with ID " + grid.getId() + " not found"));
    probe.updateGrid(gridUpdated);
  }

  public List<ProbeVisitedPosition> audit(Integer probeId) {
    return probeVisitedPositionsRepo.findByProbeId(probeId);
  }
}
