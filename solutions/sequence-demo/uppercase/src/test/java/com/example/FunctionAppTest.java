package com.example;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple function.
 */
public class FunctionAppTest {
    @Test
    public void testFunction() {
        JsonObject args = new JsonObject();
        JsonArray splitStrings = new JsonArray();
        splitStrings.add("apple");
        splitStrings.add("orange");
        splitStrings.add("banana");
        args.add("result", splitStrings);
        JsonObject response = FunctionApp.main(args);
        assertNotNull(response);
        JsonArray results = response.getAsJsonArray("result");
        assertNotNull(results);
        assertEquals(3, results.size());
        List<String> actuals = new ArrayList<>();
        results.forEach(j -> actuals.add(j.getAsString()));
        assertTrue(actuals.contains("APPLE"));
        assertTrue(actuals.contains("ORANGE"));
        assertTrue(actuals.contains("BANANA"));
    }
}
