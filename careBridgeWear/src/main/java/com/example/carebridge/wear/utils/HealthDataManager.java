package com.example.carebridge.wear.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HealthDataManager {

    // SharedPreferences file name
    private static final String PREF_HEALTH_DATA = "health_data_prefs";

    // Preference keys
    private static final String KEY_HEART_RATE_DATA = "heart_rate_data";
    private static final String KEY_STEPS_DATA = "steps_data";
    private static final String KEY_LAST_HEART_RATE = "last_heart_rate";
    private static final String KEY_LAST_STEPS = "last_steps";
    private static final String KEY_LAST_UPDATE = "last_update";

    // History limits (Wear-friendly)
    private static final int MAX_HEART_RATE_HISTORY = 100;
    private static final int MAX_STEPS_HISTORY = 50;

    // Data types
    private static final String TYPE_HEART_RATE = Constants.METRIC_HEART_RATE;
    private static final String TYPE_STEPS = Constants.METRIC_STEPS;

    // Time constants
    private static final long ONE_DAY_MILLIS = Constants.ONE_DAY_MS;

    private static final String EMPTY_JSON_ARRAY = "[]";

    private final SharedPreferences preferences;
    private final Gson gson = new Gson();

    /**
     * Constructor
     *
     * @param context Wear OS application context
     */
    public HealthDataManager(Context context) {
        preferences =
                context.getSharedPreferences(
                        PREF_HEALTH_DATA,
                        Context.MODE_PRIVATE
                );
    }

    /**
     * Represents a single health data reading.
     */
    public static class HealthDataPoint {
        public int value;
        public long timestamp;
        public String type;

        public HealthDataPoint(int value, String type) {
            this.value = value;
            this.type = type;
            this.timestamp = new Date().getTime();
        }
    }

    // =================================================
    // Save Operations
    // =================================================

    /**
     * Saves heart rate reading and updates history.
     */
    public void saveHeartRate(int heartRate) {
        if (heartRate <= 0) return;

        List<HealthDataPoint> data = getHeartRateData();
        data.add(new HealthDataPoint(heartRate, TYPE_HEART_RATE));

        trimHistory(data, MAX_HEART_RATE_HISTORY);
        saveData(KEY_HEART_RATE_DATA, data);

        preferences.edit()
                .putInt(KEY_LAST_HEART_RATE, heartRate)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    /**
     * Saves steps reading and updates history.
     */
    public void saveSteps(int steps) {
        if (steps < 0) return;

        List<HealthDataPoint> data = getStepsData();
        data.add(new HealthDataPoint(steps, TYPE_STEPS));

        trimHistory(data, MAX_STEPS_HISTORY);
        saveData(KEY_STEPS_DATA, data);

        preferences.edit()
                .putInt(KEY_LAST_STEPS, steps)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    // =================================================
    // Get Latest Values
    // =================================================

    public int getLastHeartRate() {
        return preferences.getInt(
                KEY_LAST_HEART_RATE,
                Constants.DEFAULT_HEART_RATE
        );
    }

    public int getLastSteps() {
        return preferences.getInt(
                KEY_LAST_STEPS,
                Constants.DEFAULT_STEPS
        );
    }

    public long getLastUpdateTime() {
        return preferences.getLong(KEY_LAST_UPDATE, 0);
    }

    // =================================================
    // History Access
    // =================================================

    public List<HealthDataPoint> getHeartRateData() {
        return getData(KEY_HEART_RATE_DATA);
    }

    public List<HealthDataPoint> getStepsData() {
        return getData(KEY_STEPS_DATA);
    }

    // =================================================
    // Calculations
    // =================================================

    public int getAverageHeartRate() {
        List<HealthDataPoint> data = getHeartRateData();
        if (data.isEmpty()) return getLastHeartRate();

        int sum = 0;
        for (HealthDataPoint point : data) {
            sum += point.value;
        }
        return sum / data.size();
    }

    public int getMaxHeartRate() {
        List<HealthDataPoint> data = getHeartRateData();
        if (data.isEmpty()) return getLastHeartRate();

        int max = Integer.MIN_VALUE;
        for (HealthDataPoint point : data) {
            max = Math.max(max, point.value);
        }
        return max;
    }

    public int getMinHeartRate() {
        List<HealthDataPoint> data = getHeartRateData();
        if (data.isEmpty()) return getLastHeartRate();

        int min = Integer.MAX_VALUE;
        for (HealthDataPoint point : data) {
            min = Math.min(min, point.value);
        }
        return min;
    }

    /**
     * Returns total steps taken in the last 24 hours.
     */
    public int getTotalStepsToday() {
        List<HealthDataPoint> data = getStepsData();
        if (data.isEmpty()) return getLastSteps();

        long since = System.currentTimeMillis() - ONE_DAY_MILLIS;
        int total = 0;

        for (HealthDataPoint point : data) {
            if (point.timestamp >= since) {
                total = Math.max(total, point.value);
            }
        }
        return total;
    }

    // =================================================
    // Utilities
    // =================================================

    /**
     * Clears all stored health data.
     */
    public void clearAllData() {
        preferences.edit()
                .remove(KEY_HEART_RATE_DATA)
                .remove(KEY_STEPS_DATA)
                .remove(KEY_LAST_HEART_RATE)
                .remove(KEY_LAST_STEPS)
                .remove(KEY_LAST_UPDATE)
                .apply();
    }

    private List<HealthDataPoint> getData(String key) {
        String json = preferences.getString(key, EMPTY_JSON_ARRAY);
        Type type = new TypeToken<List<HealthDataPoint>>() {}.getType();
        List<HealthDataPoint> data = gson.fromJson(json, type);
        return data != null ? data : new ArrayList<>();
    }

    private void saveData(String key, List<HealthDataPoint> data) {
        preferences.edit()
                .putString(key, gson.toJson(data))
                .apply();
    }

    private void trimHistory(List<HealthDataPoint> data, int maxSize) {
        if (data.size() > maxSize) {
            data.subList(0, data.size() - maxSize).clear();
        }
    }
}