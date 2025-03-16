Feature: Send Commands to Probe
  As an API client
  I want to get status of the probe after it is initialized

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
      "probeType": "FAST"
    }
    """
    And the response status code should be OK 2xx
    And extract int "probeId" property value from response into key="PROBE_ID"

  Scenario: Send a get status command to probe
    When I send a GET request to "/v1/probe/${PROBE_ID}/status"
    Then the response status code should be OK 2xx
    And the response body should contain: '"position":{"x":0,"y":0},"direction":"NORTH"'


