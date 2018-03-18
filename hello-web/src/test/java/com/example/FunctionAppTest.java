package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.JsonObject;

import org.junit.Test;

/**
 * Unit test for simple function.
 */
public class FunctionAppTest {
  @Test
  public void testFunction() {
    JsonObject args = new JsonObject();
    args.addProperty("name", "test");
    JsonObject response = FunctionApp.main(args);
    assertNotNull(response);
    String actual = response.get("response").getAsJsonObject().get("name").getAsString();
    assertEquals("test", actual);
  }
}
