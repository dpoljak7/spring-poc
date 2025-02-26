package com.example.demo.controller;

import com.example.api.controller.V1Api;
import com.example.api.model.ProbeState;
import com.example.api.model.V1ProbeAuditGet200ResponseInner;
import com.example.api.model.V1ProbeCommandPostRequest;
import com.example.api.model.V1ProbeInitPost201Response;
import com.example.api.model.V1ProbeInitPostRequest;
import com.example.demo.db_entity.Grid;
import com.example.demo.model.Direction;
import com.example.demo.model.Position;
import com.example.demo.service.ProbeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProbeController implements V1Api {

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
  public ResponseEntity<List<V1ProbeAuditGet200ResponseInner>> v1ProbeAuditGet() {
    log.info("Entering v1ProbeAuditGet");
    ResponseEntity<List<V1ProbeAuditGet200ResponseInner>> response = V1Api.super.v1ProbeAuditGet();
    log.info(
        "v1ProbeAuditGet successful with response: {}, HTTP status: {}",
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
    probeService.execute(probeId, command);
    ResponseEntity<Void> response = ResponseEntity.ok().build();
    log.info(
        "v1ProbeCommandPost successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }

  @Override
  public ResponseEntity<ProbeState> v1ProbeStatusGet() {
    log.info("Entering v1ProbeStatusGet");
    ResponseEntity<ProbeState> response = V1Api.super.v1ProbeStatusGet();
    log.info(
        "v1ProbeStatusGet successful with response: {}, HTTP status: {}",
        response.getBody(),
        response.getStatusCodeValue());
    return response;
  }
}
