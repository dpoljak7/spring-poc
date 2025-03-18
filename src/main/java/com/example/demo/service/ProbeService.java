package com.example.demo.service;

import com.example.api.model.generated.ProbeState;
import com.example.api.model.generated.ProbeStatePosition;
import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.dto.ProbeData;
import com.example.demo.exception.AutopilotNoPathException;
import com.example.demo.model.IOperationalProbe;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeFactory;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProbeService {

  private final ProbeFactory probeFactory;

  private final ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  private final ProbeRepo probeRepo;

  private final GridRepo gridRepo;

  private final Autopilot autopilot;

  private final CommandService commandService;

  public int initialize(ProbeData probeData) {
    Grid gridSaved = gridRepo.save(probeData.getGrid());
    Probe probe =
        Probe.builder()
            .probeType(probeData.getProbeType())
            .grid(gridSaved)
            .direction(probeData.getDirection())
            .build();
    Probe probeSaved = probeRepo.save(probe);

    ProbeVisitedPosition visitedPosition =
        ProbeVisitedPosition.builder()
            .probeId(probeSaved.getId())
            .username(SecurityContextHolder.getContext().getAuthentication().getName())
            .xCoordinate(probeData.getPosition().getX())
            .yCoordinate(probeData.getPosition().getY())
            .direction(probeData.getDirection())
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
  public void executeCommand(int probeId, String command) {

    IOperationalProbe probeImpl = probeFactory.createProbe(probeId);
    commandService.executeCommand(command, probeImpl);
  }

  public List<ProbeVisitedPosition> audit(Integer probeId) {
    return probeVisitedPositionsRepo.findByProbeId(probeId);
  }

  public void executeAutopilot(Integer probeId, Position destination) {
    IOperationalProbe probeImpl = probeFactory.createProbe(probeId);

    String commandFromAutopilot = autopilot.createCommandsForDestination(probeImpl, destination);

    if (StringUtils.isEmpty(commandFromAutopilot)) {
      throw new AutopilotNoPathException(
          "Could not find path or probe is already at destination=" + destination);
    }
    commandService.executeCommand(commandFromAutopilot, probeImpl);
  }

  public ProbeState getProbeState(Integer probeId) {
    Optional<Probe> probeOptional = probeRepo.findById(probeId);
    Probe probe =
        probeOptional.orElseThrow(
            () -> new IllegalArgumentException("Probe with ID " + probeId + " not found"));
    ProbeState probeState = new ProbeState();
    probeState.setId(probeId);
    probeState.setDirection(ProbeState.DirectionEnum.valueOf(probe.getDirection().name()));
    probeState.setPosition(
        new ProbeStatePosition().x(probe.getXCoordinate()).y(probe.getYCoordinate()));
    return probeState;
  }
}
