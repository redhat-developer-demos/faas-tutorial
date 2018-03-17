package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Base64;

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
    //the request data "{"name": "test"}" will be returned with Bas64 encoded string
    //within JSON attribute __ow_body
    String base64ReqBody = response.getAsJsonPrimitive("__ow_body").getAsString();
    String actual = new String(Base64.getDecoder().decode(base64ReqBody.getBytes()));
    assertEquals("{\"name\": \"test\"}", actual);
  }
}
