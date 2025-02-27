package com.example.demo.db_repo;

import com.example.demo.db_entity.Grid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GridRepo extends JpaRepository<Grid, Integer> {
  // Custom queries if needed
}
