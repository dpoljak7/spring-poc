package com.example.demo.service;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.exception.AutopilotNoPathException;
import com.example.demo.model.Direction;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeFactory;
import com.example.demo.model.ProbeFast;
import com.example.demo.util.WaitUtil;
import java.time.LocalDateTime;
import java.util.List;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProbeService {

  @Autowired private ProbeFactory probeFactory;

  @Autowired private ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  @Autowired private ProbeRepo probeRepo;

  @Autowired private GridRepo gridRepo;

  @Autowired private WaitUtil waitUtil;

  @Autowired private Autopilot autopilot;

  @Autowired private CommandService commandService;

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
   * <pre>
   * Method is implemented asynchronously because execution can be long if many commands are given
   * </pre>
   */
  //  @Async
  public void executeCommand(int probeId, String command) {
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
    IOperationalProbe probeImpl = probeFactory.createProbeFast(grid, probe, probe.getDirection());

    commandService.executeCommand(probeId, command, probeImpl, grid, probe);
  }

  public List<ProbeVisitedPosition> audit(Integer probeId) {
    return probeVisitedPositionsRepo.findByProbeId(probeId);
  }

  public void executeAutopilot(Integer probeId, Position destination) {
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
    IOperationalProbe probeImpl = probeFactory.createProbeFast(grid, probe, probe.getDirection());

    String commandFromAutopilot =
        autopilot.createCommandsForDestination(
            grid, probeImpl.getCurrentPosition(), probeImpl.getCurrentDirection(), destination);

    if (StringUtils.isEmpty(commandFromAutopilot)) {
      throw new AutopilotNoPathException("Could not find path or probe is already at destination=" + destination);
    }
    commandService.executeCommand(probeId, commandFromAutopilot, probeImpl, grid, probe);
  }
}
