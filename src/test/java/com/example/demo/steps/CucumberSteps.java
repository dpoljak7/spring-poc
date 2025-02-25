package com.example.demo.steps;

import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CucumberSteps {
  private boolean configWorking = false;

  @Given("I have a working Cucumber configuration")
  public void i_have_a_working_cucumber_configuration() {
    // Set a flag to indicate the configuration is set up.
    configWorking = true;
  }

  @When("I run the test from IntelliJ")
  public void i_run_the_test_from_intellij() {
    // This step can remain empty since running the test is done by the IDE.
  }

  @Then("the test should pass")
  public void the_test_should_pass() {
    // Assert that our configuration flag is true.
    assertTrue("Cucumber configuration is not working", configWorking);
  }
}
