package com.example.demo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ProbeFactory {

  @Autowired private ApplicationContext applicationContext;

  // Create and return a Probe instance using applicationContext with the provided parameters
  public IProbe createProbeSimple(Grid grid, Position position, Direction direction) {
    // Logic to pass parameters if needed can be added here

    return applicationContext.getBean(ProbeSimple.class, grid, position, direction);
  }
}
