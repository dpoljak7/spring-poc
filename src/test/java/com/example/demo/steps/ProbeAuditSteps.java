package com.example.demo.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.example.api.model.V1ProbeAuditGet200ResponseInner;
import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ProbeAuditSteps extends CommonSteps {

  @When("I send a GET request to {string} with query params:")
  public void iSendAGetRequestToWithQueryParams(String endpoint, DataTable dataTable)
      throws Exception {
    String oauth2Token = jwtUtil.generateToken(USERNAME);
    List<Map<String, String>> paramsList = dataTable.asMaps(String.class, String.class);

    MockHttpServletRequestBuilder requestBuilder =
        get(endpoint).header("Authorization", "Bearer " + oauth2Token);

    for (Map<String, String> param : paramsList) {
      String name =
          param.get("Query param"); // Replace "Column1" with the actual column header name.
      String value = param.get("Value"); // Replace "Column2" with the actual column header name.
      String valueUpdated = insertValueFromScenarioContext(value);
      requestBuilder.queryParam(name, valueUpdated);
    }

    ResultActions resultActions = mockMvc.perform(requestBuilder);
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  private String insertValueFromScenarioContext(String value) {
    if (value.trim().startsWith("${") && value.endsWith("}")) {
      String key = value.substring(2, value.length() - 1);
      return ScenarioContext.getValue(key, Object.class).toString();
    }
    return value;
  }

  @Then("response should contain the following positions:")
  public void responseShouldContainTheFollowingPositions(DataTable dataTable) throws Exception {
    List<Map<String, String>> expectedPositions = dataTable.asMaps(String.class, String.class);
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);

    String jsonResponse = result.andReturn().getResponse().getContentAsString();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    List<V1ProbeAuditGet200ResponseInner> actualPositions =
        objectMapper.readValue(
            jsonResponse, new TypeReference<List<V1ProbeAuditGet200ResponseInner>>() {});

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
