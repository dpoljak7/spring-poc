package com.example.demo.model;

import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.Probe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ProbeFactory {

  @Autowired private ApplicationContext applicationContext;

  public IOperationalProbe createProbeFast(Grid grid, Probe probe, Direction direction) {
    Position position = new Position(probe.getXCoordinate(), probe.getYCoordinate());
    return applicationContext.getBean(ProbeFast.class, probe.getId(), grid, position, direction);
  }

  public IOperationalProbe createProbeSlow(Grid grid, Probe probe, Direction direction) {
    Position position = new Position(probe.getXCoordinate(), probe.getYCoordinate());
    return applicationContext.getBean(ProbeSlow.class, probe.getId(), grid, position, direction);
  }
}
