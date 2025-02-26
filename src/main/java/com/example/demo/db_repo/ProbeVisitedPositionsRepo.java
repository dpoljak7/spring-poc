package com.example.demo.db_repo;

import com.example.demo.db_entity.ProbeVisitedPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProbeVisitedPositionsRepo extends JpaRepository<ProbeVisitedPosition, Integer> {

    @Query("SELECT p FROM ProbeVisitedPosition p WHERE p.probeId = :probeId ORDER BY p.timestampVisited DESC LIMIT 1")
    ProbeVisitedPosition findNewestByProbeId(@Param("probeId") String probeId);

    List<ProbeVisitedPosition> findByProbeIdAndCommandExecuted(String probeName, String command);

}