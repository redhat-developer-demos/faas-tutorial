package com.redhat.developers.openwhisk;

import java.io.IOException;
import java.util.Hashtable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

/**
 * TODO
 */
public class FunctionApp {

    private static final Hashtable<String, String> MEMDB = new Hashtable<>();
    private static final HttpUrl SLACK_API_URL = HttpUrl.parse("https://slack.com/api/");
    private static final String REPONSE_TEXT = "Hey %s, you said %s ";

    public static JsonObject main(JsonObject args) {
        //System.out.println("Processing Bot from Slack: " + args);

        JsonObject response = new JsonObject();
        String token = null;

        if (args.has("token")) {
            token = args.getAsJsonPrimitive("token").getAsString();
        } else {
            JsonObject headers = new JsonObject();
            headers.addProperty("Content-Type", "application/json");
            response.add("headers", headers);
            response.addProperty("statusCode", "401");
            return response;
        }

        //Request parameters
        final String requestMethod = args.getAsJsonPrimitive("__ow_method").getAsString();
        final String slackVerificationToken = args.has("slackVerificationToken")
                ? args.getAsJsonPrimitive("slackVerificationToken").getAsString()
                : null;
        final String requestType = args.has("type") ? args.getAsJsonPrimitive("type").getAsString() : null;
        final String challenge = args.has("challenge") ? args.getAsJsonPrimitive("challenge").getAsString() : null;

        if ("post".equals(requestMethod) && "url_verification".equals(requestType)
                && (token != null && token.equals(slackVerificationToken)) && challenge != null) {
            //System.out.println("Handling Verification Request");
            JsonObject headers = new JsonObject();
            headers.addProperty("Content-Type", "application/json");
            response.add("headers", headers);
            JsonObject body = new JsonObject();
            body.addProperty("challenge", challenge);
            response.add("body", body);
            //System.out.println("Sent Challenge Response :" + response);
            return response;
        }

        JsonObject owHeaders = args.getAsJsonObject("__ow_headers");

        //Handling Timeout Retries - for slow network
        if (owHeaders.has("x-slack-retry-reason")) {
            String reason = owHeaders.getAsJsonPrimitive("x-slack-retry-reason").getAsString();
            //System.out.println("Retry Reason:" + reason);
            if ("http_timeout".equalsIgnoreCase(reason)) {
                JsonObject r = new JsonObject();
                //System.out.println("Handling Event Retries");
                JsonObject headers = new JsonObject();
                r.addProperty("Content-Type", "application/json");
                r.add("headers", headers);
                r.addProperty("statusCode", 200);
                return r;
            }
        }

        //Handle the event
        //System.out.println("Handling Event from Slack: " + args);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        JsonObject headers = new JsonObject();
        headers.addProperty("Content-Type", "application/json");
        response.add("headers", headers);
        response.addProperty("statusCode", "200");

        JsonObject event = args.getAsJsonObject("event");

        final String eventType = event.has("type") ? event.getAsJsonPrimitive("type").getAsString() : null;
        final String botUserOAuthToken = args.has("botUserOAuthToken")
                ? args.getAsJsonPrimitive("botUserOAuthToken").getAsString()
                : null;
        final String user = event.has("user") ? event.getAsJsonPrimitive("user").getAsString() : null;

        if ("message".equals(eventType) && user != null) {
            RequestBody userRequest = new FormBody.Builder().add("token", botUserOAuthToken).add("user", user).build();
            Request request = new Request.Builder().url(SLACK_API_URL + "/users.info").post(userRequest).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response r) throws IOException {
                    try {
                        if (r.isSuccessful()) {
                            JsonObject responseObj = new JsonParser().parse(r.body().string()).getAsJsonObject();
                            JsonObject userObj = responseObj.get("user").getAsJsonObject();
                            postMessage(response, okHttpClient, event, botUserOAuthToken, user, userObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });

        }

        return response;
    }

    /**
     * 
     */
    private static void postMessage(JsonObject response, OkHttpClient okHttpClient, JsonObject event,
            String botUserOAuthToken, String user, JsonObject userObj) throws IOException {

        final String channel = event.has("channel") ? event.getAsJsonPrimitive("channel").getAsString() : null;
        final String text = event.has("text") ? event.getAsJsonPrimitive("text").getAsString() : "No Message!!";

        final String userName = userObj.has("real_name") ? userObj.getAsJsonPrimitive("real_name").getAsString()
                : "Anonymous";

        RequestBody messageRequest = new FormBody.Builder().add("token", botUserOAuthToken).add("channel", channel)
                .add("text", String.format(REPONSE_TEXT, userName, text)).build();

        Request request = new Request.Builder().url(SLACK_API_URL + "/chat.postMessage").post(messageRequest).build();

        Call call = okHttpClient.newCall(request);
        Response r = call.execute();
        if (r.isSuccessful()) {
            response.addProperty("status", r.message());
            response.addProperty("message", "Message sent successfully to user :" + userName);
        } else {
            response.addProperty("status", r.message());
            response.addProperty("message", "Unable to post Message to user :" + user + ", Reason:" + r.message());
        }
    }
}
