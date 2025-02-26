Feature: Submersible Probe Control
  As an operator,
  I want to control the remotely operated submersible probe with a series of commands,
  So that I can navigate the ocean floor safely and accurately.

  # Default grid: 5x5 with coordinates 0-4 in both dimensions.
  # For most scenarios, an obstacle is placed at (2,2) unless stated otherwise.

 # 1) Implement Local OAuth2 for adding user with simetric key
  # 2) Implement Probe Mock with random amount of time that executes the command
  # 3) implement probe with State machine - State: Idle, Moving, Error (no power, damaged)
  # 4) implement Command pattern
  # 5) Implement Error Handling
  # 6) implement custom endpoint to check status of the Probe - instant return
  # 7) implement API rate limiter
  # 8) Implement Audit logs with user who instructed the command
  # 9) Implement Strategy patterns - shortest path, safe path
  # 10) implement database Postgres
  #   - for audit table - user, probe id, command, timestamp
  #   - for probe statistics - probe id, temperature, pressure, salinity, ph, battery, timestamp
  #   - probe status - probe id, battery, status (idle, moving, error), location, direction
  # 11) Implement caching of summary from database, implement caching of sensory data (e.g. average temperature)
  # Documentation - unit test gifs, performance analysis gifs, database gifs, pipeline gifs
  # 12) deployment to Render
  # 13) integration tests with render (deployed app)
  # 14) Cucumber reports in documentation!
  # 15) Diagrams in readme
  # 16) code coverage percentage
  # 17) Google code coverage - formatter
# 18) implement Cucumber tests and start with TDD
# 19) implement OpenAPI yaml and generate Java code
# 20) Implement in Java code probe task with objective design
# 21) implement database connection with postgres and define liquibase schema

