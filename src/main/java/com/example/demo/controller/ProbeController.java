package com.example.demo.controller;

import com.example.api.controller.V1Api;
import com.example.api.model.ProbeState;
import com.example.api.model.V1ProbeAuditGet200ResponseInner;
import com.example.api.model.V1ProbeAutopilotPostRequest;
import com.example.api.model.V1ProbeAutopilotPostRequestDestination;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.example.demo.service.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProbeController implements V1Api {

  private final ConcurrentHashMap<Integer, ReentrantLock> probeLocks = new ConcurrentHashMap<>();

  @Autowired
  private SyncService syncService;

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
    syncService.synchronize(probeId, probeService::executeCommand, probeId, command);
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

  @Override
  public ResponseEntity<Void> v1ProbeAutopilotPost(
      V1ProbeAutopilotPostRequest v1ProbeAutopilotPostRequest) {
    log.info("Entering v1ProbeAutopilotPost");
    Integer probeId = v1ProbeAutopilotPostRequest.getProbeId();
    V1ProbeAutopilotPostRequestDestination destination =
        v1ProbeAutopilotPostRequest.getDestination();
    Position destinationPosition = new Position(destination.getX(), destination.getY());
    // Execution is locked per probeId since all commands for same probe can be executed 1 at a time
    syncService.synchronize(probeId, probeService::executeAutopilot, probeId, destinationPosition);
    ResponseEntity<Void> response = ResponseEntity.ok().build();
    log.info(
        "v1ProbeAutopilotPost successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }

  private static List<V1ProbeAuditGet200ResponseInner> mapToResponse(
      List<ProbeVisitedPosition> visitedPositions) {
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
