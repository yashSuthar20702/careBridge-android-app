package com.example.carebridge.shared.utils;

/**
 * API Configuration Constants for CareBridge Application
 *
 * This class contains all API endpoint configurations and server settings
 * for the Android application. It provides centralized management of
 * API URLs and server connections.
 */
public class ApiConstants {

    // ============================================
    // SERVER CONFIGURATION SETTINGS
    // ============================================

    /**
     * Switch between local development server and production/device server
     * Set to true during development to use Android Emulator's localhost
     * Set to false for production or testing on physical devices
     */
    public static final boolean USE_LOCALHOST = true;

    /**
     * Localhost IP for Android Emulator (10.0.2.2 points to host machine's localhost)
     * Used when USE_LOCALHOST = true
     */
    private static final String LOCALHOST_IP = "10.0.2.2";

    /**
     * Device server IP for physical devices or production
     * Used when USE_LOCHOST = false
     */
    private static final String DEVICE_SERVER_IP = "10.0.0.165";

    /**
     * Root path for all API endpoints on the server
     * This is the base directory where all PHP endpoints are located
     */
    private static final String API_ROOT = "/CareBridge/careBridge-web-app/careBridge-website/endpoints/";

    // ============================================
    // SERVER URL CONSTRUCTION HELPER
    // ============================================

    /**
     * Determines the base host IP based on the USE_LOCALHOST flag
     *
     * @return The appropriate IP address (localhost for emulator, device IP for physical devices)
     */
    private static String getBaseHost() {
        return USE_LOCALHOST ? LOCALHOST_IP : DEVICE_SERVER_IP;
    }

    // ============================================
    // BASE URL CONSTRUCTORS FOR DIFFERENT MODULES
    // ============================================

    /**
     * @return Base URL for authentication-related endpoints
     */
    public static String getAuthBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "auth/";
    }

    /**
     * @return Base URL for guardian-related endpoints
     */
    public static String getGuardianBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "guardians/";
    }

    /**
     * @return Base URL for patient-related endpoints
     */
    public static String getPatientBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "patients/";
    }

    /**
     * @return Base URL for patient-guardian assignment endpoints
     */
    public static String getPatientGuardianAssignmentBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "patientguardianassignment/";
    }

    /**
     * @return Base URL for prescription-related endpoints
     */
    public static String getPrescriptionBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "prescription/";
    }

    /**
     * @return Base URL for Firebase Cloud Messaging (FCM) endpoints
     */
    public static String getFcmBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "users/";
    }

    // ============================================
    // SPECIFIC API ENDPOINTS
    // ============================================

    /**
     * @return Complete URL for user login endpoint
     */
    public static String getLoginUrl() {
        return getAuthBaseUrl() + "login.php";
    }

    /**
     * @param guardianId The unique identifier of the guardian
     * @return Complete URL to fetch guardian details by ID
     */
    public static String getGuardianByIdUrl(String guardianId) {
        return getGuardianBaseUrl() + "getOne.php?guardian_id=" + guardianId;
    }

    /**
     * @param caseId The unique case identifier of the patient
     * @return Complete URL to fetch patient details by case ID
     */
    public static String getPatientByCaseIdUrl(String caseId) {
        return getPatientBaseUrl() + "getOne.php?case_id=" + caseId;
    }

    /**
     * @param caseId The unique case identifier of the patient
     * @return Complete URL to fetch guardian assignments for a specific patient
     */
    public static String getGuardianAssignmentByPatientUrl(String caseId) {
        return getPatientGuardianAssignmentBaseUrl() + "getByPatient.php?case_id=" + caseId;
    }

    /**
     * @param guardianId The unique identifier of the guardian
     * @return Complete URL to fetch all patients assigned to a specific guardian
     */
    public static String getAssignedPatientsUrl(String guardianId) {
        return getPatientGuardianAssignmentBaseUrl() + "getByGuardian.php?guardian_id=" + guardianId;
    }

    /**
     * @param caseId The unique case identifier of the patient
     * @return Complete URL to fetch prescriptions for a specific patient
     */
    public static String getPrescriptionByCaseIdUrl(String caseId) {
        return getPrescriptionBaseUrl() + "get.php?case_id=" + caseId;
    }

    /**
     * @return Complete URL for updating FCM token for mobile device
     */
    public static String getUpdateFcmTokenUrl() {
        return getFcmBaseUrl() + "save_fcm_token.php";
    }

    /**
     * @return Complete URL for deleting FCM token for mobile device
     */
    public static String getDeleteFcmTokenUrl() {
        return getFcmBaseUrl() + "delete_fcm_token.php";
    }

    /**
     * @return Complete URL for updating FCM token for wearable device
     */
    public static String getUpdateWearFcmTokenUrl() {
        return getFcmBaseUrl() + "save_wear_fcm_token.php";
    }

    /**
     * @return Complete URL for deleting FCM token for wearable device
     */
    public static String getDeleteWearFcmTokenUrl() {
        return getFcmBaseUrl() + "delete_wear_fcm_token.php";
    }

    /**
     * @param caseId The unique case identifier of the patient
     * @return Complete URL to fetch medicine log entries for a specific patient
     */
    public static String getMedicineLogByCaseIdUrl(String caseId) {
        return "http://" + getBaseHost() + API_ROOT + "medicine_log/get.php?case_id=" + caseId;
    }

    /**
     * @return Complete URL to fetch daily health tips
     */
    public static String getDailyTipsUrl() {
        return "http://" + getBaseHost() + API_ROOT + "daily_tips/get_tips.php";
    }

}