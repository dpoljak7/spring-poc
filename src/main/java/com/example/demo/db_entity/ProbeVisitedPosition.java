package com.example.demo.db_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "probe_id", unique = true, nullable = false)
    private String probeId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "x_coordinate", nullable = false)
    private int xCoordinate;

    @Column(name = "y_coordinate", nullable = false)
    private int yCoordinate;

    @Column(name = "command_executed", nullable = false)
    private String commandExecuted;

    @Column(name = "timestamp_visited", nullable = false)
    private LocalDateTime timestampVisited;


}