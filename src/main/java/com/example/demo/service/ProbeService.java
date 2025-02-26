package com.example.demo.service;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.model.Direction;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeFactory;
import com.example.demo.model.ProbeFast;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProbeService {

  @Autowired private ProbeFactory probeFactory;

  @Autowired private ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  @Autowired private ProbeRepo probeRepo;

  @Autowired private GridRepo gridRepo;

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
   * Method is synchronized because only one can be executed at a time
   * </pre>
   */
  //  @Async
  public synchronized void execute(int probeId, String command) {
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
      switch (cmd) {
        case 'F':
          probeImpl.moveForward();
          break;
        case 'B':
          probeImpl.moveBackward();
          break;
        case 'L':
          probeImpl.turnLeft();
          break;
        case 'R':
          probeImpl.turnRight();
          break;
        default:
          throw new IllegalArgumentException("Invalid command: " + cmd);
      }

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
  }

  public List<ProbeVisitedPosition> audit(Integer probeId) {
    return probeVisitedPositionsRepo.findByProbeId(probeId);
  }
}
