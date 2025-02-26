package com.example.demo.model;

import com.example.demo.db_entity.Grid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ProbeFactory {

  @Autowired private ApplicationContext applicationContext;

  public IOperationalProbe createProbeFast(Grid grid, Position position, Direction direction) {
    return applicationContext.getBean(ProbeFast.class, grid, position, direction);
  }

  public IOperationalProbe createProbeSlow(Grid grid, Position position, Direction direction) {
    return applicationContext.getBean(ProbeSlow.class, grid, position, direction);
  }
}
