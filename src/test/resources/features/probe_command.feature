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
        "direction": "North"
      },
      "probeType": "ProbeFast"
    }
    """
    And the response status code should be OK 2xx
    And extract int "probeId" property value from response into key="PROBE_ID"

  Scenario: Send a movement command to the probe
    Given a payload with payloadKey="PAYLOAD_KEY" enriched with valueKey="PROBE_ID"
      """
    {
      "probeId": "${PROBE_ID}",
      "command": "FFLB"
    }
    """
    When I send a POST request to "/v1/probe/command" with payloadKey="PAYLOAD_KEY"
    Then the response status code should be OK 2xx
    And the database should contain the following positions:
      | x | y | direction |
      | 0 | 0 | North     |
      | 0 | 1 | North     |
      | 0 | 2 | North     |
