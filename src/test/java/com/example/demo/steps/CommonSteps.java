package com.example.demo.steps;

import com.example.demo.db_repo.ProbeVisitedPositionsRepo;
import com.example.demo.oauth2.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

// Autowired works in Cucumber even without @Component or @Service
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CommonSteps {

  public static final String USERNAME = "user1";
  @Autowired protected MockMvc mockMvc;
  @Autowired protected JwtUtil jwtUtil;
  @Autowired protected ProbeVisitedPositionsRepo probeVisitedPositionsRepo;
}
