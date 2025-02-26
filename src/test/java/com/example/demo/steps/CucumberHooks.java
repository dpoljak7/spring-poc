package com.example.demo.steps;

import io.cucumber.java.Before;

public class CucumberHooks extends CommonSteps {

  @Before(order = 0) // Runs before any test scenario executes
  public void clearDatabase() {
    probeVisitedPositionsRepo.deleteAll();
  }
}
