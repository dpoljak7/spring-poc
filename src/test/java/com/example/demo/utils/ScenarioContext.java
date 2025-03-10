package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioContext {

  private static final Map<String, Object> context = new HashMap<>();

  public static void setValue(String key, Object value) {
    context.put(key, value);
  }

  public static <T> T getValue(String key, Class<T> type) {
    return type.cast(context.get(key));
  }

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

  /**
   * Replaces all placeholders in the given string with their corresponding values from ScenarioContext.
   *
   * @param input the string containing placeholders in the format ${VAR}
   * @return a string with all placeholders replaced by values from ScenarioContext
   * @throws IllegalArgumentException if a placeholder cannot be resolved in ScenarioContext
   */

  public static String insertValueFromScenarioContext(String input) {
    // Use a StringBuffer to build the result while replacing placeholders.
    StringBuffer result = new StringBuffer();

    // Match all `${VAR}` patterns in the input string.
    Matcher matcher = VARIABLE_PATTERN.matcher(input);

    while (matcher.find()) {
      // Extract the variable name inside ${VAR}
      String variableName = matcher.group(1);

      // Get the value from ScenarioContext
      Object replacementValue = ScenarioContext.getValue(variableName, Object.class);

      if (replacementValue == null) {
        throw new IllegalArgumentException("No value found in ScenarioContext for placeholder: " + variableName);
      }

      // Replace the placeholder ${VAR} with the actual value
      matcher.appendReplacement(result, replacementValue.toString());
    }

    // Append the remaining portion of the input string
    matcher.appendTail(result);

    return result.toString();
  }

}
