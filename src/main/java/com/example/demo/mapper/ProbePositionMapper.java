package com.example.demo.mapper;

import com.example.api.model.generated.V1ProbeProbeIdAutopilotPostRequest;
import com.example.demo.model.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProbePositionMapper {

  @Mapping(source = "destination.x", target = "x")
  @Mapping(source = "destination.y", target = "y")
  Position toPosition(V1ProbeProbeIdAutopilotPostRequest v1ProbeAutopilotPostRequest);
}
