Feature: Simple configuration check
  As a developer,
  I want to verify that my Cucumber configuration works,
  So that I can run tests from IntelliJ.

  Scenario: Verify that Cucumber configuration works
    Given I have a working Cucumber configuration
    When I run the test from IntelliJ
    Then the test should pass