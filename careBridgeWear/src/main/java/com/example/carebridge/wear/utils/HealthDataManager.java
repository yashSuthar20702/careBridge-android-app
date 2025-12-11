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
    private static final String PREFS_NAME = "HealthDataPrefs";
    private static final String KEY_HEART_RATE_DATA = "heart_rate_data";
    private static final String KEY_STEPS_DATA = "steps_data";
    private static final String KEY_LAST_HEART_RATE = "last_heart_rate";
    private static final String KEY_LAST_STEPS = "last_steps";
    private static final String KEY_LAST_UPDATE = "last_update";

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    public HealthDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static class HealthDataPoint {
        public int value;
        public long timestamp;
        public String type;

        public HealthDataPoint(int value, String type) {
            this.value = value;
            this.timestamp = new Date().getTime();
            this.type = type;
        }
    }

    public void saveHeartRate(int heartRate) {
        // Save to history
        List<HealthDataPoint> data = getHeartRateData();
        data.add(new HealthDataPoint(heartRate, "heart_rate"));

        // Keep only last 100 readings
        if (data.size() > 100) {
            data = data.subList(data.size() - 100, data.size());
        }

        saveData(KEY_HEART_RATE_DATA, data);

        // Save last reading
        prefs.edit()
                .putInt(KEY_LAST_HEART_RATE, heartRate)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    public void saveSteps(int steps) {
        // Save to history
        List<HealthDataPoint> data = getStepsData();
        data.add(new HealthDataPoint(steps, "steps"));

        // Keep only last 50 readings
        if (data.size() > 50) {
            data = data.subList(data.size() - 50, data.size());
        }

        saveData(KEY_STEPS_DATA, data);

        // Save last reading
        prefs.edit()
                .putInt(KEY_LAST_STEPS, steps)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    public int getLastHeartRate() {
        return prefs.getInt(KEY_LAST_HEART_RATE, 72); // Default 72 BPM
    }

    public int getLastSteps() {
        return prefs.getInt(KEY_LAST_STEPS, 0);
    }

    public long getLastUpdateTime() {
        return prefs.getLong(KEY_LAST_UPDATE, 0);
    }

    public List<HealthDataPoint> getHeartRateData() {
        return getData(KEY_HEART_RATE_DATA);
    }

    public List<HealthDataPoint> getStepsData() {
        return getData(KEY_STEPS_DATA);
    }

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
            if (point.value > max) {
                max = point.value;
            }
        }
        return max;
    }

    public int getMinHeartRate() {
        List<HealthDataPoint> data = getHeartRateData();
        if (data.isEmpty()) return getLastHeartRate();

        int min = Integer.MAX_VALUE;
        for (HealthDataPoint point : data) {
            if (point.value < min) {
                min = point.value;
            }
        }
        return min;
    }

    public int getTotalStepsToday() {
        List<HealthDataPoint> data = getStepsData();
        if (data.isEmpty()) return getLastSteps();

        long today = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        int total = 0;
        for (HealthDataPoint point : data) {
            if (point.timestamp > today) {
                total = Math.max(total, point.value);
            }
        }
        return total;
    }

    public void clearAllData() {
        prefs.edit()
                .remove(KEY_HEART_RATE_DATA)
                .remove(KEY_STEPS_DATA)
                .remove(KEY_LAST_HEART_RATE)
                .remove(KEY_LAST_STEPS)
                .remove(KEY_LAST_UPDATE)
                .apply();
    }

    private List<HealthDataPoint> getData(String key) {
        String json = prefs.getString(key, "[]");
        Type type = new TypeToken<List<HealthDataPoint>>(){}.getType();
        List<HealthDataPoint> data = gson.fromJson(json, type);
        return data != null ? data : new ArrayList<>();
    }

    private void saveData(String key, List<HealthDataPoint> data) {
        String json = gson.toJson(data);
        prefs.edit().putString(key, json).apply();
    }
}