package com.example.carebridge.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.carebridge.model.User;
import com.example.carebridge.model.PatientInfo;
import com.example.carebridge.utils.SharedPrefManager;
import com.google.gson.Gson;

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
    private final Context context;
    private final SharedPrefManager sharedPrefManager;
    private final OkHttpClient client;

    private static final String BASE_URL = "http://10.0.2.2/CareBridge/careBridge-web-app/careBridge-website/endpoints/auth/";
    private static final String LOGIN_URL = BASE_URL + "login.php";

    public AuthController(Context context) {
        this.context = context;
        this.sharedPrefManager = new SharedPrefManager(context);
        this.client = new OkHttpClient();
        Log.d(TAG, "[INIT] AuthController initialized");
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
            Log.d(TAG, "[LOGIN REQUEST] JSON: " + json.toString());
        } catch (JSONException e) {
            Log.e(TAG, "[LOGIN ERROR] JSON creation failed", e);
            callback.onFailure("Error creating request JSON");
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(LOGIN_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "[NETWORK ERROR] " + e.getMessage());
                mainHandler.post(() -> callback.onFailure("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resString = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "[RESPONSE] " + resString);

                mainHandler.post(() -> {
                    try {
                        JSONObject resJson = new JSONObject(resString);
                        String status = resJson.optString("status");
                        String message = resJson.optString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            JSONObject userJson = resJson.getJSONObject("user");
                            User user = new User();
                            user.setId(userJson.optInt("user_id"));
                            user.setUsername(userJson.optString("username"));
                            user.setRole(userJson.optString("role"));
                            user.setReferenceId(userJson.optString("reference_id")); // Save reference_id
                            user.setCreatedAt(userJson.optString("created_at"));

                            // Parse linked_data (PatientInfo)
                            JSONObject linkedDataJson = userJson.optJSONObject("linked_data");
                            String caseIdToSave;
                            if (linkedDataJson != null && linkedDataJson.length() > 0) {
                                PatientInfo patientInfo = new Gson().fromJson(linkedDataJson.toString(), PatientInfo.class);
                                user.setPatientInfo(patientInfo);
                                Log.d(TAG, "[PATIENT INFO] " + patientInfo.toString());

                                // Use linked_data.case_id if available, otherwise use referenceId
                                caseIdToSave = (patientInfo.getCase_id() != null && !patientInfo.getCase_id().isEmpty())
                                        ? patientInfo.getCase_id()
                                        : user.getReferenceId();

                            } else {
                                Log.w(TAG, "[PATIENT INFO] linked_data is empty or null");
                                user.setPatientInfo(new PatientInfo());
                                caseIdToSave = user.getReferenceId();
                            }

                            // Save user session + caseId + referenceId in SharedPreferences
                            final String finalCaseId = caseIdToSave;
                            new Thread(() -> {
                                sharedPrefManager.saveUserSession(user);
                                sharedPrefManager.saveCaseId(finalCaseId);
                                sharedPrefManager.saveReferenceId(user.getReferenceId());

                                Log.d(TAG, "[SESSION] User session saved: " + user.getUsername());
                                Log.d(TAG, "[SESSION] Case ID saved: " + sharedPrefManager.getCaseId());
                                Log.d(TAG, "[SESSION] Reference ID saved: " + sharedPrefManager.getReferenceId());
                            }).start();

                            callback.onSuccess(user);
                        } else {
                            callback.onFailure(message.isEmpty() ? "Login failed" : message);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "[RESPONSE ERROR] Invalid server response", e);
                        callback.onFailure("Invalid server response");
                    }
                });
            }
        });
    }

    public void logout() {
        Log.d(TAG, "[LOGOUT] Clearing session");
        sharedPrefManager.clearSession();
    }

    public boolean isLoggedIn() {
        boolean loggedIn = sharedPrefManager.isLoggedIn();
        Log.d(TAG, "[SESSION CHECK] isLoggedIn=" + loggedIn);
        return loggedIn;
    }

    public User getCurrentUser() {
        User user = sharedPrefManager.getCurrentUser();
        Log.d(TAG, "[SESSION USER] " + (user != null ? user.getUsername() : "null"));
        return user;
    }
}
