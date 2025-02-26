package com.example.demo.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.demo.model.Direction;
import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.test.web.servlet.ResultActions;

public class ProbeCommandSteps extends CommonSteps {

  @When("I send a POST request to {string} with payloadKey={string}")
  public void sendPostRequestWithPayloadKey(String endpoint, String payloadKey) throws Exception {
    String oauth2Token = jwtUtil.generateToken(USERNAME);
    String payload = ScenarioContext.getValue(payloadKey, String.class);
    ResultActions resultActions =
        mockMvc
            .perform(
                post(endpoint)
                    .header("Authorization", "Bearer " + oauth2Token)
                    .content(payload)
                    .contentType("application/json"))
            .andDo(print());
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  @Given("a payload with payloadKey={string} enriched with valueKey={string}")
  public void createPayloadWithPlaceholders(String payloadKey, String valueKey, String rawPayload)
      throws Exception {
    // Replace placeholders in the raw payload
    String enrichedPayload = rawPayload;
    Object keyValue = ScenarioContext.getValue(valueKey, Object.class);
    enrichedPayload =
        Pattern.compile("\\$\\{" + valueKey + "\\}")
            .matcher(enrichedPayload)
            .replaceAll(keyValue.toString());
    ScenarioContext.setValue(payloadKey, enrichedPayload);
  }

  @Then("the database should contain the following positions:")
  public void verifyDatabaseContainsPositions(io.cucumber.datatable.DataTable dataTable) {
    List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
    // Important: table header x | y | direction is skipped (not in the first row!)
    data.forEach(
        row -> {
          int x = Integer.parseInt(row.get("x"));
          int y = Integer.parseInt(row.get("y"));
          String direction = row.get("direction");

          assertTrue(
              probeVisitedPositionsRepo.existsByXCoordinateAndYCoordinateAndDirection(
                  x, y, Direction.valueOf(direction.toUpperCase())));
        });
  }

  @Then("the database should NOT contain the following positions:")
  public void verifyDatabaseDoesNotContainPositions(io.cucumber.datatable.DataTable dataTable) {
    List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
    // Important: table header x | y | direction is skipped (not in the first row!)
    data.forEach(
        row -> {
          int x = Integer.parseInt(row.get("x"));
          int y = Integer.parseInt(row.get("y"));
          String direction = row.get("direction");

          assertTrue(
              !probeVisitedPositionsRepo.existsByXCoordinateAndYCoordinateAndDirection(
                  x, y, Direction.valueOf(direction.toUpperCase())));
        });
  }
}
