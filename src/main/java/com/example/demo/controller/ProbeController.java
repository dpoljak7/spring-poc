package com.example.demo.controller;

import com.example.api.controller.generated.V1Api;
import com.example.api.model.generated.ProbeState;
import com.example.api.model.generated.V1ProbeInitPost201Response;
import com.example.api.model.generated.V1ProbeInitPostRequest;
import com.example.api.model.generated.V1ProbeProbeIdAuditGet200ResponseInner;
import com.example.api.model.generated.V1ProbeProbeIdAutopilotPostRequest;
import com.example.api.model.generated.V1ProbeProbeIdCommandPostRequest;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.dto.ProbeData;
import com.example.demo.mapper.ProbeMapper;
import com.example.demo.mapper.ProbePositionMapper;
import com.example.demo.mapper.ProbeVisitedPositionsMapper;
import com.example.demo.model.Position;
import com.example.demo.service.ProbeService;
import com.example.demo.service.SyncService;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProbeController implements V1Api {

  private final ConcurrentHashMap<Integer, ReentrantLock> probeLocks = new ConcurrentHashMap<>();

  private final SyncService syncService;
  private final ProbeService probeService;
  private final ProbeMapper probeMapper;
  private final ProbeVisitedPositionsMapper probeVisitedPositionsMapper;
  private final ProbePositionMapper probePositionMapper;

  @Override
  public ResponseEntity<Void> v1VerifyGet() {
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> v1WarmupGet() {
    throw new NotImplementedException();
  }

  @Override
  public ResponseEntity<V1ProbeInitPost201Response> v1ProbeInitPost(
      V1ProbeInitPostRequest request) {
    log.info("Entering v1ProbeInitPost with request: {}", request);
    ProbeData probeData = probeMapper.toProbeData(request);
    int probeId = probeService.initialize(probeData);
    V1ProbeInitPost201Response body = new V1ProbeInitPost201Response();
    body.setProbeId(probeId);
    log.info("v1ProbeInitPost successful with response OK, probeId={}", probeId);
    return ResponseEntity.ok(body);
  }

  @Override
  public ResponseEntity<Void> v1ProbeProbeIdCommandPost(
      Integer probeId, V1ProbeProbeIdCommandPostRequest v1ProbeCommandPostRequest) {
    log.info("Entering v1ProbeCommandPost with request: {}", v1ProbeCommandPostRequest);
    String command = v1ProbeCommandPostRequest.getCommand();
    // Execution is locked per probeId since all commands for same probe can be executed 1 at a time
    syncService.synchronize(probeId, probeService::executeCommand, probeId, command);
    log.info("v1ProbeCommandPost successful with response OK");
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ProbeState> v1ProbeProbeIdStatusGet(Integer probeId) {
    log.info("Entering v1ProbeStatusGet");
    ProbeState probeState = probeService.getProbeState(probeId);
    log.info("v1ProbeStatusGet successful with response OK");
    return ResponseEntity.ok(probeState);
  }

  @Override
  public ResponseEntity<List<V1ProbeProbeIdAuditGet200ResponseInner>> v1ProbeProbeIdAuditGet(
      Integer probeId) {
    log.info("Entering v1ProbeAuditGet");
    List<ProbeVisitedPosition> visitedPositions = probeService.audit(probeId);
    List<V1ProbeProbeIdAuditGet200ResponseInner> responseList =
        probeVisitedPositionsMapper.mapToResponse(visitedPositions);
    log.info("v1ProbeAuditGet successful with response OK, visitedPositions={}", visitedPositions);
    return ResponseEntity.ok(responseList);
  }

  @Override
  public ResponseEntity<Void> v1ProbeProbeIdAutopilotPost(
      Integer probeId, V1ProbeProbeIdAutopilotPostRequest v1ProbeAutopilotPostRequest) {
    log.info("Entering v1ProbeAutopilotPost");
    Position position = probePositionMapper.toPosition(v1ProbeAutopilotPostRequest);
    // Execution is locked per probeId since all commands for same probe can be executed 1 at a time
    syncService.synchronize(probeId, probeService::executeAutopilot, probeId, position);
    log.info("v1ProbeAutopilotPost successful with response OK");
    return ResponseEntity.ok().build();
  }
}
