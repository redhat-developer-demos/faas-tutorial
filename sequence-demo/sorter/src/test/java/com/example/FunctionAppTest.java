package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.junit.Test;

/**
 * Unit test for Sorted Function.
 */
public class FunctionAppTest {
  @Test
  public void testFunction() {
    JsonObject args = new JsonObject();
    JsonArray splitStrings = new JsonArray();
    splitStrings.add("APPLE");
    splitStrings.add("ORANGE");
    splitStrings.add("BANANA");
    args.add("result", splitStrings);
    JsonObject response = FunctionApp.main(args);
    assertNotNull(response);
    JsonArray results = response.getAsJsonArray("result");
    assertNotNull(results);
    assertEquals(3, results.size());
    ArrayList<String> actuals = new ArrayList<>();
    results.forEach(j -> actuals.add(j.getAsString()));
    assertTrue(actuals.get(0).equals("APPLE"));
    assertTrue(actuals.get(1).equals("BANANA"));
    assertTrue(actuals.get(2).equals("ORANGE"));
  }
}
