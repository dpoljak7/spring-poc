package com.example.demo.db_repo;

import com.example.demo.db_entity.Probe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProbeRepo extends JpaRepository<Probe, Integer> {
  // Custom queries if needed
}
