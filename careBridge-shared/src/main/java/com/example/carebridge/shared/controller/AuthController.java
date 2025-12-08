package com.example.carebridge.shared.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.shared.model.User;
import com.example.carebridge.shared.model.PatientInfo;
import com.example.carebridge.shared.utils.ApiConstants;
import com.example.carebridge.shared.utils.SharedPrefManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class AuthController {
    private static final String TAG = "AuthController";
    private final SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;

    public AuthController(Context context) {
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public void login(String username, String password, LoginCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            callback.onFailure("Error building JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(ApiConstants.getLoginUrl())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(() -> callback.onFailure("Network error"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                handler.post(() -> {
                    try {
                        JSONObject res = new JSONObject(responseStr);
                        if (!"success".equalsIgnoreCase(res.optString("status"))) {
                            callback.onFailure(res.optString("message", "Login failed"));
                            return;
                        }

                        JSONObject userJson = res.getJSONObject("user");
                        User user = new User();
                        user.setId(userJson.getInt("user_id"));
                        user.setUsername(userJson.getString("username"));
                        user.setRole(userJson.getString("role"));
                        user.setReferenceId(userJson.optString("reference_id"));
                        user.setCreatedAt(userJson.optString("created_at"));

                        JSONObject linked = userJson.optJSONObject("linked_data");
                        String caseIdToStore;

                        if (linked != null && linked.length() > 0) {
                            PatientInfo pi = new Gson().fromJson(linked.toString(), PatientInfo.class);
                            user.setPatientInfo(pi);
                            caseIdToStore = pi.getCaseId() != null ? pi.getCaseId() : user.getReferenceId();
                        } else {
                            user.setPatientInfo(new PatientInfo());
                            caseIdToStore = user.getReferenceId();
                        }

                        sharedPrefManager.saveUserSession(user);
                        sharedPrefManager.saveCaseId(caseIdToStore);
                        sharedPrefManager.saveReferenceId(user.getReferenceId());

                        callback.onSuccess(user);

                    } catch (Exception e) {
                        callback.onFailure("Invalid response");
                    }
                });
            }
        });
    }

    /** Update FCM token on server (mobile or Wear) */
    public void sendFcmTokenToServer(int userId, String fcmToken, boolean isWearToken) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", userId);
            if (isWearToken) json.put("wear_os_fcm_token", fcmToken);
            else json.put("fcm_token", fcmToken);

            String url = isWearToken ? ApiConstants.getUpdateWearFcmTokenUrl() : ApiConstants.getUpdateFcmTokenUrl();

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to update token: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "Token update response: " + response.body().string());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Token JSON error: " + e.getMessage());
        }
    }

    /** Delete token from server */
    public void deleteFcmTokenFromServer(User user, boolean isWearToken) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", user.getId());

            String url = isWearToken ? ApiConstants.getDeleteWearFcmTokenUrl() : ApiConstants.getDeleteFcmTokenUrl();
            if (isWearToken) json.put("wear_os_fcm_token", ""); else json.put("fcm_token", "");

            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url(url).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { Log.e(TAG, "Failed to delete token: " + e.getMessage()); }
                @Override
                public void onResponse(Call call, Response response) throws IOException { Log.d(TAG, "Token delete response: " + response.body().string()); }
            });
        } catch (Exception e) { Log.e(TAG, "Delete token JSON error: " + e.getMessage()); }
    }

    public boolean isLoggedIn() { return sharedPrefManager.isLoggedIn(); }
    public User getCurrentUser() { return sharedPrefManager.getCurrentUser(); }

    /** Logout & delete token */
    public void logout(boolean isWearDevice) {
        User user = getCurrentUser();
        if (user != null) deleteFcmTokenFromServer(user, isWearDevice);

        sharedPrefManager.clearSession();
        sharedPrefManager.clearCaseId();
        sharedPrefManager.clearReferenceId();
    }
}
