package com.example.demo.db_entity;

import com.example.demo.model.Direction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "probe") // Table name in the database
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Probe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
  private int id;

  @Column(name = "probe_type", nullable = false)
  private String probeType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "grid_id", nullable = false, foreignKey = @ForeignKey(name = "fk_probe_grid"))
  private Grid grid; // Foreign key linking to the Grid entity

  @Column(name = "x_coordinate", nullable = false) // X-coordinate within the grid
  private int xCoordinate;

  @Column(name = "y_coordinate", nullable = false) // Y-coordinate within the grid
  private int yCoordinate;

  @Enumerated(EnumType.STRING) // Persist the enum as a string (e.g., "NORTH", "SOUTH", etc.)
  @Column(
      name = "direction",
      nullable = false,
      length = 50) // Matches the VARCHAR(50) in the SQL schema
  private Direction direction; // Uses the Direction enum
}
