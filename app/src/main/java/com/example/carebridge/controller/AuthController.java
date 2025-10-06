package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.User;
import com.example.carebridge.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthController {

    private static final String TAG = "AuthController";
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;

    // API URL for emulator localhost
    private static final String LOGIN_URL = "http://10.0.2.2/CareBridge/careBridge-website/endpoints/auth/login.php";

    public AuthController(Context context) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String message);
    }

    public void login(String username, String password, LoginCallback callback) {
        Log.d(TAG, "login() called with username: " + username);

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error: ", e);
            callback.onFailure("Internal error creating request JSON");
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        Log.d(TAG, "Sending login request to " + LOGIN_URL + " with body: " + json.toString());

        client.newCall(request).enqueue(new Callback() {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network request failed: ", e);
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resString = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Response received: " + resString);

                mainHandler.post(() -> {
                    try {
                        JSONObject resJson = new JSONObject(resString);

                        String status = resJson.optString("status");
                        String message = resJson.optString("message");

                        Log.d(TAG, "Response status: " + status + ", message: " + message);

                        if ("success".equalsIgnoreCase(status)) {
                            JSONObject userJson = resJson.getJSONObject("user");

                            User user = new User();
                            user.setId(userJson.optInt("user_id"));
                            user.setUsername(userJson.optString("username"));
                            user.setRole(userJson.optString("role"));
                            user.setName(userJson.optString("username"));
                            user.setEmail(userJson.optString("email", ""));
                            user.setPhone(userJson.optString("phone", ""));
                            user.setAddress(userJson.optString("address", ""));

                            Log.d(TAG, "User parsed successfully: " + user.getUsername());

                            // Save session (in background to avoid UI blocking)
                            new Thread(() -> {
                                sharedPrefManager.saveUserSession(user);
                                Log.d(TAG, "User session saved successfully");
                            }).start();

                            callback.onSuccess(user);
                        } else {
                            Log.e(TAG, "Login failed: " + message);
                            callback.onFailure(message);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: ", e);
                        callback.onFailure("Invalid response from server");
                    }
                });
            }
        });
    }

    public void logout() {
        Log.d(TAG, "logout() called");
        sharedPrefManager.clearSession();
        Log.d(TAG, "Session cleared");
    }

    public boolean isLoggedIn() {
        boolean loggedIn = sharedPrefManager.isLoggedIn();
        Log.d(TAG, "isLoggedIn(): " + loggedIn);
        return loggedIn;
    }

    public User getCurrentUser() {
        User user = sharedPrefManager.getCurrentUser();
        Log.d(TAG, "getCurrentUser(): " + (user != null ? user.getUsername() : "null"));
        return user;
    }
}
