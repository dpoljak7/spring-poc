Feature: Probe initialization

  Scenario: Initialize probe via API endpoint
    When I send a POST request to "/v1/probe/init" with the following payload:
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
    Then the response status code should be OK 2xx
    And extract int "probeId" property value from response into key="PROBE_ID"
    And database has audit log for probe stored in key="PROBE_ID" with command="INIT"
    And database has probe found by key="PROBE_ID"