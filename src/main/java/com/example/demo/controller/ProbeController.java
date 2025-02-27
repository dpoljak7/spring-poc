package com.example.demo.controller;

import com.example.api.controller.V1Api;
import com.example.api.model.ProbeState;
import com.example.api.model.V1ProbeAuditGet200ResponseInner;
import com.example.api.model.V1ProbeCommandPostRequest;
import com.example.api.model.V1ProbeInitPost201Response;
import com.example.api.model.V1ProbeInitPostRequest;
import com.example.demo.db_entity.Grid;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.model.Direction;
import com.example.demo.model.Position;
import com.example.demo.service.ProbeService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProbeController implements V1Api {

  private final ConcurrentHashMap<Integer, ReentrantLock> probeLocks = new ConcurrentHashMap<>();

  @Value("${probeCommandLockTimeoutSec}")
  private int probeCommandLockTimeoutSec;

  @Autowired private ProbeService probeService;

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
    List<String> obstacles =
        request.getObstacles().stream()
            .map(obstacle -> obstacle.getX() + "," + obstacle.getY())
            .toList();

    Grid grid =
        Grid.builder()
            .xSize(request.getGridSize().getWidth())
            .ySize(request.getGridSize().getHeight())
            .obstacles(obstacles)
            .build();
    Direction direction = Direction.valueOf(request.getInitialPosition().getDirection().name());
    Position position =
        new Position(request.getInitialPosition().getX(), request.getInitialPosition().getY());
    int probeId = probeService.initialize(grid, position, direction);
    V1ProbeInitPost201Response body = new V1ProbeInitPost201Response();
    body.setProbeId(probeId);
    ResponseEntity<V1ProbeInitPost201Response> response = ResponseEntity.ok().body(body);
    log.info(
        "v1ProbeInitPost successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }

  @Override
  public ResponseEntity<Void> v1ProbeCommandPost(
      V1ProbeCommandPostRequest v1ProbeCommandPostRequest) {
    log.info("Entering v1ProbeCommandPost with request: {}", v1ProbeCommandPostRequest);
    String command = v1ProbeCommandPostRequest.getCommand();
    int probeId = v1ProbeCommandPostRequest.getProbeId();

    // Execution is locked per probeId since all commands for same probe can be executed 1 at a time
    ReentrantLock lock = probeLocks.computeIfAbsent(probeId, id -> new ReentrantLock());
    boolean lockAcquired = false;
    try {
      probeCommandLockTimeoutSec = 2;
      lockAcquired = lock.tryLock(probeCommandLockTimeoutSec, java.util.concurrent.TimeUnit.SECONDS);
      if (lockAcquired) {
        probeService.execute(probeId, command);
      } else {
        log.warn("Could not acquire lock for probeId {} within timeout", probeId);
        return ResponseEntity.status(503).build(); // Service Unavailable due to lock timeout
      }
    } catch (InterruptedException e) {
      log.error("Thread interrupted while waiting for lock for probeId {}: {}", probeId, e.getMessage());
      Thread.currentThread().interrupt();
      return ResponseEntity.status(500).build(); // Internal Server Error
    } finally {
      if (lockAcquired) {
        lock.unlock();
      }
    }
    ResponseEntity<Void> response = ResponseEntity.ok().build();
    log.info(
      "v1ProbeCommandPost successful with response: {}, HTTP status: {}",
      response.getBody(),
      response.getStatusCodeValue());
    return response;
  }

  @Override
  public ResponseEntity<ProbeState> v1ProbeStatusGet(Integer probeId) {
    log.info("Entering v1ProbeStatusGet");
    ResponseEntity<ProbeState> response = V1Api.super.v1ProbeStatusGet(probeId);
    log.info(
        "v1ProbeStatusGet successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }

  @Override
  public ResponseEntity<List<V1ProbeAuditGet200ResponseInner>> v1ProbeAuditGet(Integer probeId) {
    log.info("Entering v1ProbeAuditGet");
    List<ProbeVisitedPosition> visitedPositions = probeService.audit(probeId);
    List<V1ProbeAuditGet200ResponseInner> responseList = mapToResponse(visitedPositions);
    ResponseEntity<List<V1ProbeAuditGet200ResponseInner>> response =
        ResponseEntity.ok(responseList);
    log.info(
        "v1ProbeAuditGet successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }

  private static List<V1ProbeAuditGet200ResponseInner> mapToResponse(List<ProbeVisitedPosition> visitedPositions) {
    List<V1ProbeAuditGet200ResponseInner> responseList =
        visitedPositions.stream()
                        .map(
                visitedPosition -> {
                  V1ProbeAuditGet200ResponseInner responseInner =
                      new V1ProbeAuditGet200ResponseInner();
                  responseInner.setUser(visitedPosition.getUsername());
                  responseInner.setX(visitedPosition.getXCoordinate());
                  responseInner.setY(visitedPosition.getYCoordinate());
                  V1ProbeAuditGet200ResponseInner.DirectionEnum directionEnum =
                      V1ProbeAuditGet200ResponseInner.DirectionEnum.valueOf(
                          visitedPosition.getDirection().name());
                  responseInner.setDirection(directionEnum);
                  responseInner.setCommand(visitedPosition.getCommandExecuted());

                  OffsetDateTime timestamp = convertTime(visitedPosition.getTimestampVisited());
                  responseInner.setTimestamp(timestamp);
                  return responseInner;
                })
                        .toList();
    return responseList;
  }

  private static OffsetDateTime convertTime(LocalDateTime localDateTime) {
    ZoneId zoneId = ZoneId.systemDefault();
    ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());
    return localDateTime.atOffset(zoneOffset);
  }

}
