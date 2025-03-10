Feature: Send Commands to Probe
  As an API client
  I want to send movement commands to the probe
  So that I can verify the expected positions are reflected in the database

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
          "x": 2,
          "y": 2
        },
        {
          "x": 3,
          "y": 4
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

  Scenario: Send a movement command to the probe FFLB
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "command": "FFLB"
    }
    """
    When I send a POST request to "/v1/probe/${PROBE_ID}/command" with payloadKey="PAYLOAD_KEY"
    Then the response status code should be OK 2xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
      | 0 | 1 | NORTH     |
      | 0 | 2 | NORTH     |


  Scenario: Send a movement command to the probe LF to go outside of the grid which causes Client Error 4xx
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "command": "LF"
    }
    """
    When I send a POST request to "/v1/probe/${PROBE_ID}/command" with payloadKey="PAYLOAD_KEY"
    Then the response status code should be Client Error 4xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
    And the database should NOT contain the following positions:
      | x  | y | direction |
      | -1 | 0 | WEST      |


  Scenario: Send a movement command to the probe FFRFF to get to obstacle at 2,2 and which causes Client Error 4xx
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "command": "FFRFF"
    }
    """
    When I send a POST request to "/v1/probe/${PROBE_ID}/command" with payloadKey="PAYLOAD_KEY"
    Then the response status code should be Client Error 4xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
      | 0 | 1 | NORTH     |
      | 0 | 2 | NORTH     |
      | 0 | 2 | EAST      |
      | 1 | 2 | EAST      |
    And the database should NOT contain the following positions:
      | x | y | direction |
      | 2 | 2 | EAST      |

