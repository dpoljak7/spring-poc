package com.example.demo.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.utils.ScenarioContext;
import com.example.demo.utils.ScenarioKeys;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.web.servlet.ResultActions;

public class AppStatusSteps extends CommonSteps {

  @When("I send a GET request to {string} no auth2")
  public void i_send_a_get_request_to(String endpoint) throws Exception {
    ResultActions resultActions = mockMvc.perform(get("/v1/verify"));
    ScenarioContext.setValue(ScenarioKeys.MOCK_MVC_RESPONSE, resultActions);
  }

  @Then("the response status code should be OK 2xx")
  public void the_response_status_code_should_be() throws Exception {
    ResultActions result =
        ScenarioContext.getValue(ScenarioKeys.MOCK_MVC_RESPONSE, ResultActions.class);
    result.andExpect(status().is2xxSuccessful()).andDo(print());
  }
}
