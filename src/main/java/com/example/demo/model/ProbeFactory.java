package com.example.demo.model;

import com.example.demo.db_entity.Probe;
import com.example.demo.db_repo.GridRepo;
import com.example.demo.db_repo.ProbeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProbeFactory {

  private final ApplicationContext applicationContext;
  private final ProbeRepo probeRepo;
  private final GridRepo gridRepo;

  public IOperationalProbe createProbeFast(Probe probe) {
    return applicationContext.getBean(ProbeFast.class, probe, gridRepo);
  }

  private IOperationalProbe createProbeSlow(Probe probe) {
    ProbeFast probeFast = applicationContext.getBean(ProbeFast.class, probe, gridRepo);
    return applicationContext.getBean(ProbeSlow.class, probeFast);
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
