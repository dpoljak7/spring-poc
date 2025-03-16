package com.example.demo.steps;

import com.example.demo.DemoApplication;
import com.example.demo.config.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@Suite
@SpringBootTest
@CucumberContextConfiguration
@AutoConfigureMockMvc
@ContextConfiguration(classes = {DemoApplication.class, TestConfig.class}) //
public class CucumberEntryPointIT {}
