package com.example.carebridge.wear.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carebridge.shared.model.User;
import com.google.gson.Gson;

public class WearSharedPrefManager {

    private static final String PREF_NAME = "CareBridgeWearPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_CASE_ID = "case_id";
    private static final String KEY_REFERENCE_ID = "reference_id";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    // NEW: Saved user_id for API calls
    private static final String KEY_USER_ID = "user_id";

    // NEW: Health data keys
    private static final String KEY_HEART_RATE = "heart_rate";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_SPO2 = "spo2";
    private static final String KEY_LAST_SYNC = "last_sync_time";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public WearSharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    /** Save full user session */
    public void saveUserSession(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Store correct user ID
        editor.putInt(KEY_USER_ID, user.getId());

        if (user.getPatientInfo() != null && user.getPatientInfo().getCaseId() != null) {
            editor.putString(KEY_CASE_ID, user.getPatientInfo().getCaseId());
        }

        if (user.getReferenceId() != null && !user.getReferenceId().isEmpty()) {
            editor.putString(KEY_REFERENCE_ID, user.getReferenceId());
        }

        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getCurrentUser() {
        String json = sharedPreferences.getString(KEY_USER, null);
        return json != null ? gson.fromJson(json, User.class) : null;
    }

    /** Get stored User ID (for delete token API) */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /** Clear only token when watch disconnects */
    public void clearWearFcmTokenOnly() {
        editor.remove(KEY_FCM_TOKEN).apply();
    }

    /** Full Logout */
    public void logout() {
        editor.clear().apply();
    }

    public void saveCaseId(String caseId) {
        editor.putString(KEY_CASE_ID, caseId).apply();
    }

    public String getCaseId() {
        return sharedPreferences.getString(KEY_CASE_ID, "");
    }

    public void clearCaseId() {
        editor.remove(KEY_CASE_ID).apply();
    }

    public void saveReferenceId(String refId) {
        editor.putString(KEY_REFERENCE_ID, refId).apply();
    }

    public String getReferenceId() {
        return sharedPreferences.getString(KEY_REFERENCE_ID, "");
    }

    public void clearReferenceId() {
        editor.remove(KEY_REFERENCE_ID).apply();
    }

    public void saveFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token).apply();
    }

    public String getFcmToken() {
        return sharedPreferences.getString(KEY_FCM_TOKEN, "");
    }

    public void clearFcmToken() {
        editor.remove(KEY_FCM_TOKEN).apply();
    }

    // ==============================
    // Wear OS Health Data Storage
    // ==============================
    public void saveHeartRate(int bpm) {
        editor.putInt(KEY_HEART_RATE, bpm).apply();
    }

    public int getHeartRate() {
        return sharedPreferences.getInt(KEY_HEART_RATE, 0);
    }

    public void saveSteps(int count) {
        editor.putInt(KEY_STEPS, count).apply();
    }

    public int getSteps() {
        return sharedPreferences.getInt(KEY_STEPS, 0);
    }

    public void saveSpo2(int spo2) {
        editor.putInt(KEY_SPO2, spo2).apply();
    }

    public int getSpo2() {
        return sharedPreferences.getInt(KEY_SPO2, 0);
    }

    public void saveLastSyncTime(String time) {
        editor.putString(KEY_LAST_SYNC, time).apply();
    }

    public String getLastSyncTime() {
        return sharedPreferences.getString(KEY_LAST_SYNC, "");
    }
}
