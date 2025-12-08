package com.example.carebridge.shared.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.DailyTipResponse;
import com.example.carebridge.shared.model.Tip;
import com.example.carebridge.shared.model.Video;
import com.example.carebridge.shared.utils.ApiConstants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DailyTipsController {

    private static final String TAG = "DailyTipsController";
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface DailyTipCallback {
        void onTips(List<Tip> tips);
        void onVideo(Video video);
        void onFailure(String error);
    }

    public void fetchDailyTips(DailyTipCallback callback) {
        String url = ApiConstants.getDailyTipsUrl();
        Log.d(TAG, "Starting fetchDailyTips. API URL: " + url);

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network call failed: " + e.getMessage(), e);
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "API Response received. Success=" + response.isSuccessful() + " Body=" + res);

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server returned error code: " + response.code());
                    mainHandler.post(() -> callback.onFailure("Server error: " + response.code()));
                    return;
                }

                mainHandler.post(() -> parseResponse(res, callback));
            }
        });
    }

    private void parseResponse(String json, DailyTipCallback callback) {
        Log.d(TAG, "Parsing response JSON: " + json);
        try {
            Gson gson = new Gson();
            DailyTipResponse dailyTipResponse = gson.fromJson(json, DailyTipResponse.class);

            if (dailyTipResponse == null) {
                Log.e(TAG, "Parsed response is null");
                callback.onFailure("Invalid response (null)");
                return;
            }

            if (!dailyTipResponse.isSuccess()) {
                Log.e(TAG, "Response success=false");
                callback.onFailure("Invalid response (success=false)");
                return;
            }

            String type = dailyTipResponse.getType();
            Log.d(TAG, "Response type: " + type);

            if ("tip".equalsIgnoreCase(type)) {
                List<Tip> tips = dailyTipResponse.getTipList();
                Log.d(TAG, "Tips count: " + (tips != null ? tips.size() : 0));
                callback.onTips(tips);
            } else if ("video".equalsIgnoreCase(type)) {
                Video video = dailyTipResponse.getVideo();
                Log.d(TAG, "Video received: " + (video != null ? video.getTitle() : "null"));
                callback.onVideo(video);
            } else {
                Log.e(TAG, "Unknown type received: " + type);
                callback.onFailure("Unknown type: " + type);
            }

        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON parsing error", e);
            callback.onFailure("JSON error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error while parsing response", e);
            callback.onFailure("Unexpected error: " + e.getMessage());
        }
    }
}
