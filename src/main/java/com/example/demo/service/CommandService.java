package com.example.demo.service;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.exception.CommandException;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.util.WaitUtil;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommandService {

  @Value("${probeMove.retriesCount}")
  private int probeMoveRetriesCount;

  @Value("${probeMove.retryDelaySec}")
  private int proveMoveRetryDelaySec;

  @Autowired GridRepo gridRepo;

  @Autowired ProbeRepo probeRepo;

  @Autowired ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  @Autowired WaitUtil waitUtil;

  public void executeCommand(
      int probeId, String command, IOperationalProbe probeImpl, Grid grid, Probe probe) {
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
            "Failed to execute command "
                + cmd
                + " after "
                + probeMoveRetriesCount
                + " retries with retryDelay="
                + proveMoveRetryDelaySec);
      }

      updateProbeMoveData(probeId, cmd, probeImpl, probe);
    }
  }

  private void updateProbeMoveData(
      int probeId, char cmd, IOperationalProbe probeImpl, Probe probe) {
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
    Grid gridUpdated =
        gridRepo
            .findById(grid.getId())
            .orElseThrow(
                () -> new IllegalStateException("Grid with ID " + grid.getId() + " not found"));
    probe.updateGrid(gridUpdated);
  }
}
