package com.redhat.developers.openwhisk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Hashtable;

public class DemoMain {


    private static final Hashtable<String, String> MEMDB = new Hashtable<>();
    private static final HttpUrl SLACK_API_URL = HttpUrl.parse("https://slack.com/api");
    private static final String REPONSE_TEXT = "Hey %s, you said %s ";

    public static void main(String[] args) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        String botUserOAuthToken = "xoxb-347428117411-kOv2agZFhRDiDqUhoKjuRBH2";
        String user = "U9SKXM8QN";

        RequestBody requestBody = new FormBody.Builder()
            .add("token", botUserOAuthToken)
            .add("user", user)
            .build();

        Request request = new Request.Builder()
            .url(SLACK_API_URL + "/users.info")
            .post(requestBody)
            .build();

        Call call = okHttpClient.newCall(request);

        try {
            Response r = call.execute();
            String str = r.body().string();
            JsonObject responseObj = new JsonParser().parse(str).getAsJsonObject();
            JsonObject userObj = responseObj.get("user").getAsJsonObject();

            final String userName = userObj.has("real_name") ?
                userObj.getAsJsonPrimitive("real_name").getAsString() : "Anonymous";

            RequestBody messageRequest = new FormBody.Builder()
                .add("token", botUserOAuthToken)
                .add("channel", "DA7FKK29G")
                .add("text", String.format(REPONSE_TEXT, userName, "From DemoMain"))
                .build();

            request = new Request.Builder()
                .url(SLACK_API_URL + "/chat.postMessage")
                .post(messageRequest)
                .build();

            call = okHttpClient.newCall(request);
            r = call.execute();

            if (r.isSuccessful()) {
                System.out.println("Successfull!");
            } else {
                System.err.println("Failed:" + r.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
