package com.example.carebridge.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carebridge.model.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static final String PREF_NAME = "CareBridgePref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_CASE_ID = "case_id"; // New key

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SharedPrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    // Save user session
    public void saveUserSession(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Save caseId separately if available
        if (user.getPatientInfo() != null && user.getPatientInfo().getCase_id() != null) {
            editor.putString(KEY_CASE_ID, user.getPatientInfo().getCase_id());
        }

        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getCurrentUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public void logout() {
        clearSession();
    }

    // New methods for caseId
    public void saveCaseId(String caseId) {
        editor.putString(KEY_CASE_ID, caseId);
        editor.apply();
    }

    public String getCaseId() {
        return sharedPreferences.getString(KEY_CASE_ID, "");
    }
}
