package com.example.demo.steps;

// import com.example.service.ProbeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class ProbeInitSteps extends CommonSteps {

  @When("I send a POST request to {string} with the following payload:")
  public void iSendAPostRequestToWithPayload(String endpoint, String payload) throws Exception {
    String oauth2Token = jwtUtil.generateAdminToken(USERNAME);
    ResultActions resultActions =
        mockMvc.perform(
            post(endpoint)
                .header("Authorization", "Bearer " + oauth2Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  @Then("the response body should contain: {string}")
  public void theResponseBodyShouldContain(String expectedBodyContent) throws Exception {
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);
    String response = result.andReturn().getResponse().getContentAsString();
    Assertions.assertThat(response.contains(expectedBodyContent)).isTrue();
  }

  @Then("database has audit log for probe stored in key={string} with command={string}")
  public void databaseHasAuditForProbeWithCommand(String key, String command) {
    Integer probeId = ScenarioContext.getValue(key, Integer.class);
    List<ProbeVisitedPosition> auditEntries =
        probeVisitedPositionsRepo.findByProbeIdAndCommandExecuted(probeId, command);
    Assertions.assertThat(auditEntries).isNotEmpty();
  }

  @Then("extract int {string} property value from response into key={string}")
  public void extractPropertyValueFromResponseIntoKey(String property, String scenarioKey)
      throws Exception {
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);
    String response = result.andReturn().getResponse().getContentAsString();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(response);
    int extractedValue = jsonNode.get(property).asInt();
    ScenarioContext.setValue(scenarioKey, extractedValue);
  }

  @Then("database has probe found by key={string}")
  public void databaseHasProbe(String key) {
    int probeId = ScenarioContext.getValue(key, Integer.class);
    boolean exists = probeRepo.existsById(probeId);
    Assertions.assertThat(exists).isTrue();
  }
}
