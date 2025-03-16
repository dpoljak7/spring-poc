package com.example.demo.mapper;

import com.example.api.model.generated.V1ProbeProbeIdAuditGet200ResponseInner;
import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.model.Direction;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProbeVisitedPositionsMapper {

  // Map a single ProbeVisitedPosition to V1ProbeProbeIdAuditGet200ResponseInner
  @Mapping(source = "XCoordinate", target = "x")
  @Mapping(source = "YCoordinate", target = "y")
  @Mapping(source = "direction", target = "direction", qualifiedByName = "mapDirection")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "commandExecuted", target = "command")
  @Mapping(
      source = "timestampVisited",
      target = "timestamp",
      qualifiedByName = "mapToOffsetDateTime")
  V1ProbeProbeIdAuditGet200ResponseInner toAuditResponse(ProbeVisitedPosition position);

  // Map a list of ProbeVisitedPosition to a list of V1ProbeProbeIdAuditGet200ResponseInner
  List<V1ProbeProbeIdAuditGet200ResponseInner> mapToResponse(
      List<ProbeVisitedPosition> visitedPositions);

  // Convert LocalDateTime to OffsetDateTime
  @Named("mapToOffsetDateTime")
  static OffsetDateTime mapToOffsetDateTime(LocalDateTime localDateTime) {
    ZoneId zoneId = ZoneId.systemDefault();
    ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());
    return localDateTime.atOffset(zoneOffset);
  }

  @Named("mapDirection")
  static V1ProbeProbeIdAuditGet200ResponseInner.DirectionEnum mapDirection(Direction direction) {
    return V1ProbeProbeIdAuditGet200ResponseInner.DirectionEnum.valueOf(direction.name());
  }
}
