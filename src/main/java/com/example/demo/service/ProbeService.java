package com.example.demo.service;

import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.model.Direction;
import com.example.demo.model.Grid;
import com.example.demo.model.IProbe;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeFactory;
import com.example.demo.model.ProbeSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProbeService {

  @Autowired
  private ProbeFactory probeFactory;

  @Autowired
  private ProbeVisitedPositionsRepo probeVisitedPositionsRepo;

  private IProbe probe;

  public ProbeService() {
    // Initialize the command map with the corresponding actions
    //    commandMap.put('F', this::moveForward);
    //    commandMap.put('B', this::moveBackward);
    //    commandMap.put('L', this::turnLeft);
    //    commandMap.put('R', this::turnRight);
  }

  public void initialize(Grid grid, Position position, Direction direction) {
    //    probe = probeFactory.createProbeSimple(grid, position, direction);
    ProbeVisitedPosition visitedPosition = ProbeVisitedPosition.builder()
                                                               .probeId(ProbeSimple.class.getSimpleName())
                                                               .username(SecurityContextHolder.getContext().getAuthentication().getName())
                                                               .xCoordinate(position.getX())
                                                               .yCoordinate(position.getY())
                                                               .commandExecuted("INIT")
                                                               .timestampVisited(LocalDateTime.now())
                                                               .build();
    probeVisitedPositionsRepo.save(visitedPosition);

  }

  //  public void executeCommand(String command) {
  //    command.toUpperCase().chars()
  //           .mapToObj(c -> (char) c)
  //           .forEach(c -> commandMap.getOrDefault(c, () -> {
  //           }).run());
  //  }

}
