package com.example.carebridge.wear.utils;

import androidx.annotation.StringRes;

import com.example.carebridge.wear.R;

@SuppressWarnings("unused") // Required for centralized resource management
public final class StringResources {

    private StringResources() {
        // Prevent instantiation
    }

    // ================= Home Buttons =================
    @StringRes public static final int BUTTON_CALL = R.string.call;
    @StringRes public static final int BUTTON_MEDICINE = R.string.medicine;
    @StringRes public static final int BUTTON_PATIENT_HEALTH = R.string.patient_health;
    @StringRes public static final int BUTTON_GUARDIAN_INFO = R.string.guardian_info;
    @StringRes public static final int BUTTON_HEALTH_MONITOR = R.string.health_monitor;
    @StringRes public static final int BUTTON_LOGOUT = R.string.logout;

    // ================= Health Metrics =================
    @StringRes public static final int HEART_RATE_LABEL = R.string.heart_rate;
    @StringRes public static final int STEPS_LABEL = R.string.steps;
    @StringRes public static final int BLOOD_OXYGEN_LABEL = R.string.blood_oxygen;

    @StringRes public static final int UNIT_BPM = R.string.unit_bpm;
    @StringRes public static final int UNIT_STEPS = R.string.unit_steps;
    @StringRes public static final int UNIT_PERCENT = R.string.unit_percent;

    // ================= Error Messages =================
    @StringRes public static final int ERROR_NO_INTERNET = R.string.error_no_internet;
    @StringRes public static final int ERROR_LOAD_FAILED = R.string.error_load_failed;
    @StringRes public static final int ERROR_CALL_FAILED = R.string.error_call_failed;

    // ================= Notifications =================
    @StringRes public static final int ACTION_TAKEN = R.string.action_taken;
    @StringRes public static final int ACTION_NOT_TAKEN = R.string.action_not_taken;

    // ================= Emergency =================
    @StringRes public static final int EMERGENCY_NUMBER = R.string.emergency_number;
}