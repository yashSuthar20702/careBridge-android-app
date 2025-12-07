package com.example.carebridge.wear.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carebridge.shared.model.User;
import com.google.gson.Gson;

/**
 * Shared Preference manager specifically for Wear OS app
 */
public class WearSharedPrefManager {

    private static final String PREF_NAME = "CareBridgeWearPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_CASE_ID = "case_id";
    private static final String KEY_REFERENCE_ID = "reference_id";
    private static final String KEY_FCM_TOKEN = "fcm_token";

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

        if (user.getPatientInfo() != null && user.getPatientInfo().getCaseId() != null) {
            editor.putString(KEY_CASE_ID, user.getPatientInfo().getCaseId());
        }

        if (user.getReferenceId() != null && !user.getReferenceId().isEmpty()) {
            editor.putString(KEY_REFERENCE_ID, user.getReferenceId());
        }

        editor.apply();
    }

    /** Check if user is logged in */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /** Get current logged-in user */
    public User getCurrentUser() {
        String json = sharedPreferences.getString(KEY_USER, null);
        return json != null ? gson.fromJson(json, User.class) : null;
    }

    /** Clear full session */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    /** Logout user */
    public void logout() {
        clearSession();
    }

    /** Save case ID */
    public void saveCaseId(String caseId) {
        editor.putString(KEY_CASE_ID, caseId);
        editor.apply();
    }

    /** Get case ID */
    public String getCaseId() {
        return sharedPreferences.getString(KEY_CASE_ID, "");
    }

    /** Clear case ID */
    public void clearCaseId() {
        editor.remove(KEY_CASE_ID);
        editor.apply();
    }

    /** Save reference ID */
    public void saveReferenceId(String refId) {
        editor.putString(KEY_REFERENCE_ID, refId);
        editor.apply();
    }

    /** Get reference ID */
    public String getReferenceId() {
        return sharedPreferences.getString(KEY_REFERENCE_ID, "");
    }

    /** Clear reference ID */
    public void clearReferenceId() {
        editor.remove(KEY_REFERENCE_ID);
        editor.apply();
    }

    /** Save FCM token */
    public void saveFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
    }

    /** Get FCM token */
    public String getFcmToken() {
        return sharedPreferences.getString(KEY_FCM_TOKEN, "");
    }

    /** Clear FCM token */
    public void clearFcmToken() {
        editor.remove(KEY_FCM_TOKEN);
        editor.apply();
    }
}