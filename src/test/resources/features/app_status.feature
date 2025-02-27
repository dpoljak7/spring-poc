Feature: Verify and Warmup Endpoints

  Scenario: Verify initialization endpoint
    When I send a GET request to "/v1/verify" no auth2
    Then the response status code should be OK 2xx

  Scenario: Verify initialization endpoint
    When I send a GET request to "/v1/warmup" no auth2
    Then the response status code should be OK 2xx