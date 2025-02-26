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

#
#  Background:
#    Given a grid of size 5x5
#    And the following obstacles exist:
#      | x | y |
#      | 2 | 2 |
#
#
#  Scenario: Initialize probe via API endpoint
#    When I send a POST request to "/v1/probe/init" with the following payload:
#
#    Then the response status code should be OK 2xx
#    And the response body should contain:
#    """
#    {
#      "message": "Probe initialized successfully",
#      "location": {
#        "x": 0,
#        "y": 0
#      },
#      "direction": "North",
#      "status": "Idle"
#    }
#    """
#
#  Scenario: Moving forward within grid boundaries
#  """
#{
#  "probe": {
#    "currentPosition": {
#      "x": 0,
#      "y": 1,
#      "direction": "North"
#    },
#    "visitedCoordinates": [
#      { "x": 0, "y": 0 },
#      { "x": 0, "y": 1 }
#    ],
#    "status": "Moved successfully"
#  }
#}
#"""
#    Given the probe is initialized at (0,0) facing "North"
#    When I send the command "F"
#    Then the probe should be at (0,1)
#    And the probe should be facing "North"
#    And the visited coordinates should be:
#      | x | y |
#      | 0 | 0 |
#      | 0 | 1 |
#
#  Scenario: Turning left without moving
#    Given the probe is initialized at (0,0) facing "North"
#    When I send the command "L"
#    Then the probe should remain at (0,0)
#    And the probe should be facing "West"
#    And the visited coordinates should be:
#      | x | y |
#      | 0 | 0 |
#
#  Scenario: Combined turning and moving forward/backward
#    Given the probe is initialized at (1,1) facing "North"
#    When I send the command "RRF"
#    Then the probe should be at (1,0)
#    And the probe should be facing "South"
#    And the visited coordinates should be:
#      | x | y |
#      | 1 | 1 |
#      | 1 | 0 |
#
#  Scenario: Complex movement sequence without obstacles interfering
#    # Override default obstacles for this scenario
#    Given the probe is initialized at (1,2) facing "East"
#    And there are no obstacles in the grid
#    When I send the command "FFLB"
#    # Calculation:
#    # Start: (1,2), facing East
#    # F -> (2,2)
#    # F -> (3,2)
#    # L -> now facing North
#    # B -> moves backward (i.e. opposite to North, which is South) to (3,1)
#    Then the probe should be at (3,1)
#    And the probe should be facing "North"
#    And the visited coordinates should be:
#      | x | y |
#      | 1 | 2 |
#      | 2 | 2 |
#      | 3 | 2 |
#      | 3 | 1 |
#
#  Scenario: Prevent moving off the grid
#    Given the probe is initialized at (0,4) facing "North"
#    When I send the command "F"
#    Then the probe should remain at (0,4)
#    And the probe should be facing "North"
#    And the visited coordinates should be:
#      | x | y |
#      | 0 | 4 |
#
#  Scenario: Obstacle avoidance when moving forward
#    Given the probe is initialized at (2,1) facing "North"
#    When I send the command "F"
#    Then the probe should remain at (2,1)
#    And the probe should be facing "North"
#    And the system should indicate that an obstacle prevented moving into (2,2)
#    And the visited coordinates should be:
#      | x | y |
#      | 2 | 1 |
#
#  Scenario: No command execution leaves probe unchanged
#    Given the probe is initialized at (3,3) facing "South"
#    When I send no command
#    Then the probe should be at (3,3)
#    And the probe should be facing "South"
#    And the visited coordinates should be:
#      | x | y |
#      | 3 | 3 |
#
#  Scenario: Handling of invalid commands
#    Given the probe is initialized at (0,0) facing "North"
#    When I send the command "X"
#    Then the system should return an error message "Invalid command"
#    And the probe should remain at (0,0)
#    And the visited coordinates should be:
#      | x | y |
#      | 0 | 0 |