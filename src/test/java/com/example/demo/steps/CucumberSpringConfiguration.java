package com.example.demo.steps;

import com.example.demo.DemoApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = DemoApplication.class)
public class CucumberSpringConfiguration {
  // This class is used to hook Cucumber with Spring Boot.
}
