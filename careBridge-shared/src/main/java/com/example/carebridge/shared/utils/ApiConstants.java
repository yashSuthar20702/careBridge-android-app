package com.example.carebridge.shared.utils;

import android.util.Log;

/**
 * API Configuration Constants for CareBridge Application
 *
 * Centralizes all API endpoint configurations and server settings
 * for the Android application.
 */
public class ApiConstants {

    private static final String TAG = "ApiConstants";

    // ============================================
    // SERVER CONFIGURATION SETTINGS
    // ============================================

    /**
     * true  → Android Emulator (10.0.2.2)
     * false → Physical device or production server IP
     */
    public static final boolean USE_LOCALHOST = false;

    private static final String LOCALHOST_IP = "10.0.2.2";
    private static final String DEVICE_SERVER_IP = "10.0.0.165";

    /** Root server path for all PHP endpoints */
    private static final String API_ROOT =
            "/careBridge/careBridge-web-app/careBridge-website/endpoints/";

    // ============================================
    // URL HELPERS
    // ============================================

    private static String getBaseHost() {
        String host = USE_LOCALHOST ? LOCALHOST_IP : DEVICE_SERVER_IP;
        Log.d(TAG, "Selected Host: " + host);
        return host;
    }

    private static String buildBaseUrl(String moduleName) {
        String url = "http://" + getBaseHost() + API_ROOT + moduleName + "/";
        Log.d(TAG, "Base URL for module (" + moduleName + "): " + url);
        return url;
    }

    // ============================================
    // BASE MODULE URLS
    // ============================================

    public static String getAuthBaseUrl() { return buildBaseUrl("auth"); }
    public static String getGuardianBaseUrl() { return buildBaseUrl("guardians"); }
    public static String getPatientBaseUrl() { return buildBaseUrl("patients"); }
    public static String getPatientGuardianAssignmentBaseUrl() { return buildBaseUrl("patientguardianassignment"); }
    public static String getPrescriptionBaseUrl() { return buildBaseUrl("prescription"); }
    public static String getFcmBaseUrl() { return buildBaseUrl("users"); }
    public static String getMedicineLogBaseUrl() { return buildBaseUrl("medicine_log"); }
    public static String getDailyTipsBaseUrl() { return buildBaseUrl("daily_tips"); }

    /** NEW MODULE: Meal Plan */
    public static String getMealPlanBaseUrl() { return buildBaseUrl("mealplan"); }

    // ============================================
    // SPECIFIC ENDPOINT URLS
    // ============================================

    public static String getLoginUrl() {
        String url = getAuthBaseUrl() + "login.php";
        Log.d(TAG, "Login URL: " + url);
        return url;
    }

    public static String getGuardianByIdUrl(String guardianId) {
        String url = getGuardianBaseUrl() + "getOne.php?guardian_id=" + guardianId;
        Log.d(TAG, "Guardian By ID URL: " + url);
        return url;
    }

    public static String getPatientByCaseIdUrl(String caseId) {
        String url = getPatientBaseUrl() + "getOne.php?case_id=" + caseId;
        Log.d(TAG, "Patient By Case ID URL: " + url);
        return url;
    }

    public static String getGuardianAssignmentByPatientUrl(String caseId) {
        String url = getPatientGuardianAssignmentBaseUrl() + "getByPatient.php?case_id=" + caseId;
        Log.d(TAG, "Guardian Assignment By Patient URL: " + url);
        return url;
    }

    public static String getAssignedPatientsUrl(String guardianId) {
        String url = getPatientGuardianAssignmentBaseUrl()
                + "getByGuardian.php?guardian_id=" + guardianId;
        Log.d(TAG, "Assigned Patients URL: " + url);
        return url;
    }

    public static String getPrescriptionByCaseIdUrl(String caseId) {
        String url = getPrescriptionBaseUrl() + "get.php?case_id=" + caseId;
        Log.d(TAG, "Prescription By Case ID URL: " + url);
        return url;
    }

    // FCM
    public static String getUpdateFcmTokenUrl() {
        String url = getFcmBaseUrl() + "save_fcm_token.php";
        Log.d(TAG, "Update FCM Token URL: " + url);
        return url;
    }

    public static String getDeleteFcmTokenUrl() {
        String url = getFcmBaseUrl() + "delete_fcm_token.php";
        Log.d(TAG, "Delete FCM Token URL: " + url);
        return url;
    }

    public static String getUpdateWearFcmTokenUrl() {
        String url = getFcmBaseUrl() + "save_wear_fcm_token.php";
        Log.d(TAG, "Update Wear FCM Token URL: " + url);
        return url;
    }

    public static String getDeleteWearFcmTokenUrl() {
        String url = getFcmBaseUrl() + "delete_wear_fcm_token.php";
        Log.d(TAG, "Delete Wear FCM Token URL: " + url);
        return url;
    }

    public static String getMedicineLogByCaseIdUrl(String caseId) {
        String url = getMedicineLogBaseUrl() + "get.php?case_id=" + caseId;
        Log.d(TAG, "Medicine Log By Case ID URL: " + url);
        return url;
    }

    public static String getDailyTipsUrl() {
        String url = getDailyTipsBaseUrl() + "get_tips.php";
        Log.d(TAG, "Daily Tips URL: " + url);
        return url;
    }

    public static String getAddMealPlanUrl() {
        String url = getMealPlanBaseUrl() + "addmealplan.php";
        Log.d(TAG, "Add Meal Plan URL: " + url);
        return url;
    }

    public static String getMealPlanUrl() {
        String url = getMealPlanBaseUrl() + "getmealplan.php";
        Log.d(TAG, "Get Meal Plan URL: " + url);
        return url;
    }
}
