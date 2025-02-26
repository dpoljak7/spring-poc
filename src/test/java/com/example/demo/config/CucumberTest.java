package com.example.demo.config;

import com.example.demo.DemoApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(classes = {DemoApplication.class, TestConfig.class}) //
@AutoConfigureMockMvc
public class CucumberTest {
  // This class is used to hook Cucumber with Spring Boot.
}
