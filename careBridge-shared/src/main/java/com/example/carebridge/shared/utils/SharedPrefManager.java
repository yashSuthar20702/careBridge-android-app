package com.example.carebridge.shared.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.carebridge.shared.model.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static final String PREF_NAME = "CareBridgePref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_CASE_ID = "case_id";
    private static final String KEY_REFERENCE_ID = "reference_id";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Save user session
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

    // Login state
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Return logged in user
    public User getCurrentUser() {
        String json = sharedPreferences.getString(KEY_USER, null);
        return json != null ? gson.fromJson(json, User.class) : null;
    }

    // Clear full session
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public void logout() {
        clearSession();
    }

    // CaseId functions
    public void saveCaseId(String caseId) {
        editor.putString(KEY_CASE_ID, caseId);
        editor.apply();
    }

    public String getCaseId() {
        return sharedPreferences.getString(KEY_CASE_ID, "");
    }

    public void clearCaseId() {
        editor.remove(KEY_CASE_ID);
        editor.apply();
    }

    // Reference ID functions
    public void saveReferenceId(String refId) {
        editor.putString(KEY_REFERENCE_ID, refId);
        editor.apply();
    }

    public String getReferenceId() {
        return sharedPreferences.getString(KEY_REFERENCE_ID, "");
    }

    public void clearReferenceId() {
        editor.remove(KEY_REFERENCE_ID);
        editor.apply();
    }
}