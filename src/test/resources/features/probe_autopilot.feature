Feature: Running Autopilot to navigate to destination

  #  Grid - x is obstacle and d is destination, s is start
  #  _ _ _ x d
  #  _ x _ x _
  #  _ _ _ x _
  #  _ x x x _
  #  _ _ _ _ _
  Background:
    Given I send a POST request to "/v1/probe/init" with the following payload:
    """
    {
      "gridSize": {
        "width": 5,
        "height": 5
      },
      "obstacles": [
        {
          "x": 1,
          "y": 1
        },
        {
          "x": 2,
          "y": 1
        },
        {
          "x": 3,
          "y": 1
        },
        {
          "x": 3,
          "y": 2
        },
        {
          "x": 3,
          "y": 3
        },
        {
          "x": 3,
          "y": 4
        },
        {
          "x": 1,
          "y": 3
        }
      ],
      "initialPosition": {
        "x": 0,
        "y": 0,
        "direction": "NORTH"
      },
      "probeType": "ProbeFast"
    }
    """
    And the response status code should be OK 2xx
    And extract int "probeId" property value from response into key="PROBE_ID"

  Scenario: Send a autopilot request for 4,4 destination
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "destination": {
        "x": 4,
        "y": 4
       }
    }
    """
    When I send a POST request to "/v1/probe/${PROBE_ID}/autopilot" with payloadKey="PAYLOAD_KEY"
    Then the response status code should be OK 2xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
      | 0 | 0 | EAST      |
      | 1 | 0 | EAST      |
      | 2 | 0 | EAST      |
      | 3 | 0 | EAST      |
      | 4 | 0 | EAST      |
      | 4 | 0 | NORTH     |
      | 4 | 1 | NORTH     |
      | 4 | 2 | NORTH     |
      | 4 | 3 | NORTH     |
      | 4 | 4 | NORTH     |