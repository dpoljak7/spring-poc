package com.example.demo.db_entity;

import com.example.demo.model.Position;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grid") // Table name in the database
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grid {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
  private int id;

  @Column(name = "x_size", nullable = false)
  private int xSize;

  @Column(name = "y_size", nullable = false)
  private int ySize;

  @Column(name = "obstacles", columnDefinition = "TEXT[]") // Map to PostgreSQL TEXT[] type
  private List<String> obstacles; // Stores obstacles as a list of "x,y" strings

  public boolean isValid(Position position) {
    if (position == null) {
      return false;
    }
    int x = position.getX();
    int y = position.getY();
    String positionString = x + "," + y; // Convert Position to string format
    return x >= 0 && x < xSize && y >= 0 && y < ySize && !obstacles.contains(positionString);
  }
}
