package com.example.demo.service;

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
      String command, IOperationalProbe probeImpl) {
    for (char cmd : command.toUpperCase().toCharArray()) {
      boolean success = false;
      for (int retries = 0; retries < probeMoveRetriesCount; retries++) {
        success = executeMoveCommand(cmd, probeImpl);

        if (success) {
          break; // Exit retry loop if the move was successful
        } else {
          waitUtil.wait(proveMoveRetryDelaySec);
          probeImpl.updateGridFromDatabase();
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

      updateProbeMoveData(cmd, probeImpl);
    }
  }

  private void updateProbeMoveData(char cmd, IOperationalProbe probeImpl) {
    Probe probe = probeImpl.getProbe();
    ProbeVisitedPosition visitedPosition =
        ProbeVisitedPosition.builder()
            .probeId(probe.getId())
            .username(SecurityContextHolder.getContext().getAuthentication().getName())
            .xCoordinate(probeImpl.getProbe().getXCoordinate())
            .yCoordinate(probeImpl.getProbe().getYCoordinate())
            .direction(probeImpl.getProbe().getDirection())
            .commandExecuted(String.valueOf(cmd))
            .timestampVisited(LocalDateTime.now())
            .build();
    probeVisitedPositionsRepo.save(visitedPosition);

    //upate all probe data in database
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
}
