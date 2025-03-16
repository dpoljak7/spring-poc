package com.example.demo.dto;

import com.example.demo.db_entity.Grid;
import com.example.demo.model.Direction;
import com.example.demo.model.Position;
import com.example.demo.model.ProbeType;
import lombok.Data;

@Data
public class ProbeData {

  private Grid grid;
  private Position position;
  private Direction direction;
  private ProbeType probeType;
}
