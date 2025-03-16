package com.example.demo.model;

import com.example.demo.db_entity.Probe;
import com.example.demo.db_repo.ProbeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProbeFactory {

  private final ApplicationContext applicationContext;
  private final ProbeRepo probeRepo;

  public IOperationalProbe createProbeFast(Probe probe) {
    return applicationContext.getBean(ProbeFast.class, probe);
  }

  private IOperationalProbe createProbeSlow(Probe probe) {
    return applicationContext.getBean(ProbeSlow.class, probe);
  }

  public IOperationalProbe createProbe(Integer probeId) {
    Probe probe =
      probeRepo
        .findById(probeId)
        .orElseThrow(
          () -> new IllegalArgumentException("Probe with ID " + probeId + " not found"));

    return switch (probe.getProbeType()) {
      case FAST -> createProbeFast(probe);
      case SLOW -> createProbeSlow(probe);
    };
  }
}
