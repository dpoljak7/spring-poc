package com.example.demo.db_repo;

import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.model.Direction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeVisitedPositionsRepo extends JpaRepository<ProbeVisitedPosition, Integer> {

  @Query(
      "SELECT p FROM ProbeVisitedPosition p WHERE p.probeId = :probeId ORDER BY p.timestampVisited DESC LIMIT 1")
  ProbeVisitedPosition findNewestByProbeId(@Param("probeId") int probeId);

  List<ProbeVisitedPosition> findByProbeIdAndCommandExecuted(int probeId, String command);

  List<ProbeVisitedPosition> findByProbeId(int probeId);

  @Query(
      "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM ProbeVisitedPosition p WHERE p.xCoordinate = :xCoordinate AND p.yCoordinate = :yCoordinate AND p.direction = :direction")
  boolean existsByXCoordinateAndYCoordinateAndDirection(
      @Param("xCoordinate") int xCoordinate,
      @Param("yCoordinate") int yCoordinate,
      @Param("direction") Direction direction);
}
