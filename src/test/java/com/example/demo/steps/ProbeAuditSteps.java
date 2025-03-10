package com.example.demo.steps;

import com.example.api.model.V1ProbeProbeIdAuditGet200ResponseInner;
import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ProbeAuditSteps extends CommonSteps {

  @When("I send a GET request to {string}")
  public void iSendAGetRequestToWithQueryParams(String endpoint) throws Exception {
    iSendAGetRequestToWithQueryParams(endpoint, null);
  }

  @When("I send a GET request to {string} with query params:")
  public void iSendAGetRequestToWithQueryParams(String endpoint, DataTable dataTable)
      throws Exception {
    String oauth2Token = jwtUtil.generateAdminToken(USERNAME);
    List<Map<String, String>> paramsList = Collections.emptyList();
    if( dataTable != null) {
      paramsList = dataTable.asMaps(String.class, String.class);
    }


    String endpointUpdated = ScenarioContext.insertValueFromScenarioContext(endpoint);
    MockHttpServletRequestBuilder requestBuilder =
        get(endpointUpdated).header("Authorization", "Bearer " + oauth2Token);

    for (Map<String, String> param : paramsList) {
      String name =
          param.get("Query param"); // Replace "Column1" with the actual column header name.
      String value = param.get("Value"); // Replace "Column2" with the actual column header name.
      String valueUpdated = ScenarioContext.insertValueFromScenarioContext(value);
      requestBuilder.queryParam(name, valueUpdated);
    }

    ResultActions resultActions = mockMvc.perform(requestBuilder);
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  @Then("response should contain the following positions:")
  public void responseShouldContainTheFollowingPositions(DataTable dataTable) throws Exception {
    List<Map<String, String>> expectedPositions = dataTable.asMaps(String.class, String.class);
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);

    String jsonResponse = result.andReturn().getResponse().getContentAsString();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    List<V1ProbeProbeIdAuditGet200ResponseInner> actualPositions =
        objectMapper.readValue(
            jsonResponse, new TypeReference<List<V1ProbeProbeIdAuditGet200ResponseInner>>() {});

    for (Map<String, String> expectedPosition : expectedPositions) {
      Assertions.assertThat(actualPositions)
          .anyMatch(
              actual ->
                  actual.getX().toString().equals(expectedPosition.get("x"))
                      && actual.getY().toString().equals(expectedPosition.get("y"))
                      && actual.getDirection().name().equals(expectedPosition.get("direction")));
    }
  }
}
