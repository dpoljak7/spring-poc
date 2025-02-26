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
        "direction": "North"
      }
    }
    """
    Then the response status code should be OK 2xx
    And database has audit for probe "ProbeSimple" with command="INIT"