package com.example.carebridge.shared.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.utils.ApiConstants;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class MealController {

    private static final String TAG = "MealController";
    private final OkHttpClient client = new OkHttpClient();

    // --------------------------
    // Callbacks
    // --------------------------
    public interface MealAddCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }

    public interface MealFetchCallback {
        void onSuccess(JSONObject mealPlan);
        void onFailure(String message);
    }

    // --------------------------
    // Add Meal Plan
    // --------------------------
    public void addMealPlan(
            String caseId,
            String guardianId,
            String mealDate,
            String morningMeal,
            String afternoonMeal,
            String eveningMeal,
            String nightMeal,
            String extraInfo,
            MealAddCallback callback
    ) {
        Log.d(TAG, "🔹 Preparing JSON payload...");
        Log.d(TAG, "case_id=" + caseId + ", guardian_id=" + guardianId + ", meal_date=" + mealDate);

        JSONObject json = new JSONObject();
        try {
            json.put("case_id", caseId);
            json.put("guardian_id", guardianId);
            json.put("meal_date", mealDate);
            json.put("morning_meal", morningMeal);
            json.put("afternoon_meal", afternoonMeal);
            json.put("evening_meal", eveningMeal);
            json.put("night_meal", nightMeal);
            json.put("extra_information", extraInfo);
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed: " + e.getMessage(), e);
            callback.onFailure("Error creating JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        String url = ApiConstants.getAddMealPlanUrl();
        Log.d(TAG, "🌍 API URL → " + url);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Handler handler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "❌ Network request failed: " + e.getMessage(), e);
                handler.post(() -> callback.onFailure("Network connection error"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body() != null ? response.body().string() : null;
                handler.post(() -> {
                    try {
                        if (resStr == null || resStr.trim().isEmpty()) {
                            callback.onFailure("Empty response from server");
                            return;
                        }

                        JSONObject res = new JSONObject(resStr);
                        boolean status = res.optBoolean("status", false);

                        if (!status) {
                            callback.onFailure(res.optString("error", "Failed to add meal"));
                            return;
                        }

                        callback.onSuccess(res.optString("message", "Meal plan added successfully"));
                    } catch (Exception e) {
                        Log.e(TAG, "❌ JSON parsing failed: " + e.getMessage(), e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }

    // --------------------------
    // Fetch Meal Plan by case_id ONLY
    // --------------------------
    public void fetchMealPlanByCaseId(String caseId, MealFetchCallback callback) {
        Log.d(TAG, "Fetching meal plan for case_id=" + caseId);

        JSONObject json = new JSONObject();
        try {
            json.put("case_id", caseId);
        } catch (Exception e) {
            Log.e(TAG, "JSON creation failed: " + e.getMessage(), e);
            callback.onFailure("Error creating request JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        String url = ApiConstants.getMealPlanUrl();
        Log.d(TAG, "API URL → " + url);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Handler handler = new Handler(Looper.getMainLooper());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request failed: " + e.getMessage(), e);
                handler.post(() -> callback.onFailure("Network connection error"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resStr = response.body() != null ? response.body().string() : null;
                handler.post(() -> {
                    try {
                        if (resStr == null || resStr.trim().isEmpty()) {
                            callback.onFailure("Empty response from server");
                            return;
                        }

                        JSONObject res = new JSONObject(resStr);
                        boolean status = res.optBoolean("status", false);

                        if (!status) {
                            callback.onFailure(res.optString("error", "Meal plan not found"));
                            return;
                        }

                        JSONObject mealPlan = res.optJSONObject("meal_plan");
                        if (mealPlan == null) {
                            callback.onFailure("Meal plan data missing");
                            return;
                        }

                        callback.onSuccess(mealPlan);
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing failed: " + e.getMessage(), e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }
}
