package com.example.carebridge.utils;

/**
 * API configuration utility providing centralized endpoint management and host configuration
 */
public class ApiConstants {

    // Development flag to toggle between local testing and production server
    public static final boolean USE_LOCALHOST = false;

    // Host configuration for different environments
    private static final String LOCALHOST_IP = "10.0.2.2"; // Android emulator localhost
//    private static final String DEVICE_SERVER_IP = "10.0.0.165"; // Production server

    private static final String DEVICE_SERVER_IP = "10.144.99.26"; // Production server

    // Dynamic host selection based on environment flag
    public static String getBaseHost() {
        return USE_LOCALHOST ? LOCALHOST_IP : DEVICE_SERVER_IP;
    }

    // API path configuration - centralized root endpoint
    private static final String API_ROOT = "/CareBridge/careBridge-web-app/careBridge-website/endpoints/";

    // Base URL generators for different API modules
    public static String getAuthBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "auth/";
    }

    public static String getGuardianBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "guardians/";
    }

    public static String getPatientBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "patients/";
    }

    public static String getPatientGuardianAssignmentBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "patientguardianassignment/";
    }

    public static String getPrescriptionBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "prescription/";
    }

    // ------------------------------------------------------
    // ⭐ NEW: FCM API BASE URL
    // ------------------------------------------------------
    public static String getFcmBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "users/";
    }

    public static String getUpdateFcmTokenUrl() {
        return getFcmBaseUrl() + "save_fcm_token.php";
    }

    public static String getDeleteFcmTokenUrl() {
        return getFcmBaseUrl() + "delete_fcm_token.php";
    }

    // Specific API endpoint generators
    public static String getLoginUrl() {
        return getAuthBaseUrl() + "login.php";
    }

    public static String getGuardianByIdUrl(String guardianId) {
        return getGuardianBaseUrl() + "getOne.php?guardian_id=" + guardianId;
    }

    public static String getPatientByCaseIdUrl(String caseId) {
        return getPatientBaseUrl() + "getOne.php?case_id=" + caseId;
    }

    public static String getGuardianAssignmentByPatientUrl(String caseId) {
        return getPatientGuardianAssignmentBaseUrl() + "getByPatient.php?case_id=" + caseId;
    }

    public static String getAssignedPatientsUrl(String guardianId) {
        return getPatientGuardianAssignmentBaseUrl() + "getByGuardian.php?guardian_id=" + guardianId;
    }

    public static String getPrescriptionByCaseIdUrl(String caseId) {
        return getPrescriptionBaseUrl() + "get.php?case_id=" + caseId;
    }
}
