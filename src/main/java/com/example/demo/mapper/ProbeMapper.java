package com.example.demo.mapper;

import com.example.api.model.generated.V1ProbeInitPostRequest;
import com.example.api.model.generated.V1ProbeInitPostRequestInitialPosition;
import com.example.api.model.generated.V1ProbeInitPostRequestObstaclesInner;
import com.example.demo.dto.ProbeData;
import com.example.demo.model.Direction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProbeMapper {

  @Mapping(target = "grid.id", ignore = true) // Ignore mapping for Grid.id
  @Mapping(source = "gridSize.width", target = "grid.XSize") // Map grid width
  @Mapping(source = "gridSize.height", target = "grid.YSize") // Map grid height
  @Mapping(
      source = "obstacles",
      target = "grid.obstacles",
      qualifiedByName = "mapObstacles") // Map obstacles to grid.obstacles
  @Mapping(
      target = "position",
      expression =
          "java(new com.example.demo.model.Position(request.getInitialPosition().getX(), request.getInitialPosition().getY()))") // Map Position
  @Mapping(
      source = "initialPosition.direction",
      target = "direction",
      qualifiedByName = "mapDirection") // Map direction
  ProbeData toProbeData(V1ProbeInitPostRequest request);

  @Named("mapObstacles")
  static List<String> mapObstacles(List<V1ProbeInitPostRequestObstaclesInner> obstacles) {
    return obstacles.stream().map(obstacle -> obstacle.getX() + "," + obstacle.getY()).toList();
  }

  @Named("mapDirection")
  static Direction mapObstacles(V1ProbeInitPostRequestInitialPosition.DirectionEnum direction) {
    return Direction.valueOf(direction.name());
  }
}
