package com.example.carebridge.shared.utils;

public class ApiConstants {
    public static final boolean USE_LOCALHOST = true;
    private static final String LOCALHOST_IP = "10.0.2.2";
    private static final String DEVICE_SERVER_IP = "10.0.0.165";
    private static final String API_ROOT = "/CareBridge/careBridge-web-app/careBridge-website/endpoints/";

    private static String getBaseHost() {
        return USE_LOCALHOST ? LOCALHOST_IP : DEVICE_SERVER_IP;
    }

    // Base URLs
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

    public static String getFcmBaseUrl() {
        return "http://" + getBaseHost() + API_ROOT + "users/";
    }

    // Specific endpoints
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

    public static String getUpdateFcmTokenUrl() {
        return getFcmBaseUrl() + "save_fcm_token.php";
    }

    public static String getDeleteFcmTokenUrl() {
        return getFcmBaseUrl() + "delete_fcm_token.php";
    }

    public static String getUpdateWearFcmTokenUrl() {
        return getFcmBaseUrl() + "save_wear_fcm_token.php";
    }

    public static String getDeleteWearFcmTokenUrl() {
        return getFcmBaseUrl() + "delete_wear_fcm_token.php";
    }

    public static String getMedicineLogByCaseIdUrl(String caseId) {
        return "http://" + getBaseHost() + API_ROOT + "medicine_log/get.php?case_id=" + caseId;
    }

    public static String getDailyTipsUrl() {
        return "http://" + getBaseHost() + API_ROOT + "daily_tips/get_tips.php";  // whatever your php file path is
    }



}
