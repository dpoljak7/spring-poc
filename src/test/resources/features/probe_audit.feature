Feature: Audit API Testing

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

  Scenario: Verify the audit endpoint returns the expected response
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "command": "FFRF"
    }
    """
    And I send a POST request to "/v1/probe/${PROBE_ID}/command" with payloadKey="PAYLOAD_KEY"
    And the response status code should be OK 2xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
      | 0 | 1 | NORTH     |
      | 0 | 2 | NORTH     |
      | 0 | 2 | EAST      |
      | 1 | 2 | EAST      |
    When I send a GET request to "/v1/probe/${PROBE_ID}/audit"
    Then the response status code should be OK 2xx
    And response should contain the following positions:
      | x | y | direction |
      | 0 | 0 | NORTH     |
      | 0 | 1 | NORTH     |
      | 0 | 2 | NORTH     |
      | 0 | 2 | EAST      |
      | 1 | 2 | EAST      |

