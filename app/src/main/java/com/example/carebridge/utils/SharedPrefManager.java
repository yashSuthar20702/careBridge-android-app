package com.example.carebridge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.carebridge.model.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static final String PREF_NAME = "CareBridgePref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";

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

    public void saveUserSession(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
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
}