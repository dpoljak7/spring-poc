package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

  private static final Map<String, Object> context = new HashMap<>();

  public static void setValue(String key, Object value) {
    context.put(key, value);
  }

  public static <T> T getValue(String key, Class<T> type) {
    return type.cast(context.get(key));
  }
}
