package com.example.demo.config;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
public class CucumberRunner {
  // This class remains empty; it's used only as a holder for the above annotations.
}
