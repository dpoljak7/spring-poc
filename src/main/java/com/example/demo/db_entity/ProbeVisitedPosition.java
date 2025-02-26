package com.example.demo.db_entity;

import com.example.demo.model.Direction;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "probe_visited_position") // Table name in the database
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbeVisitedPosition {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
  private int id;

  @Column(name = "probe_id", nullable = false)
  private int probeId;

  @Column(name = "username", nullable = false, length = 50) // VARCHAR(50) in SQL
  private String username;

  @Column(name = "x_coordinate", nullable = false)
  private int xCoordinate;

  @Column(name = "y_coordinate", nullable = false)
  private int yCoordinate;

  @Column(name = "command_executed", nullable = false, length = 255) // Matches VARCHAR(255)
  private String commandExecuted;

  @Column(name = "timestamp_visited", nullable = false)
  private LocalDateTime timestampVisited;

  @ManyToOne
  @JoinColumn(name = "probe_id", insertable = false, updatable = false) // Foreign key reference
  private Probe probe;

  @Enumerated(EnumType.STRING) // Persist the enum as a string (e.g., "NORTH", "SOUTH", etc.)
  @Column(
      name = "direction",
      nullable = false,
      length = 50) // Matches the VARCHAR(50) in the SQL schema
  private Direction direction; // Uses the Direction enum
}
