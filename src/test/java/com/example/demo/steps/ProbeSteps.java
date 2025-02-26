package com.example.demo.steps;

// import com.example.service.ProbeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.demo.db_entity.ProbeVisitedPosition;
import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

public class ProbeSteps extends CommonSteps {

  @When("I send a POST request to {string} with the following payload:")
  public void iSendAPostRequestToWithPayload(String endpoint, String payload) throws Exception {
    String oauth2Token = jwtUtil.generateToken(USERNAME);
    ResultActions resultActions =
      mockMvc.perform(
        post(endpoint)
          .header("Authorization", "Bearer " + oauth2Token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(payload));
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  @Then("the response body should contain:")
  public void theResponseBodyShouldContain(String expectedBodyContent) throws Exception {
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);
    String response = result.andReturn().getResponse().getContentAsString();
    Assertions.assertThat(response.contains(expectedBodyContent)).isTrue();
  }

  @Then("database has audit for probe {string} with command={string}")
  public void databaseHasAuditForProbeWithCommand(String probeName, String command) {
    List<ProbeVisitedPosition> auditEntries = probeVisitedPositionsRepo.findByProbeIdAndCommandExecuted(probeName, command);
    Assertions.assertThat(auditEntries).isNotEmpty();
  }

}
