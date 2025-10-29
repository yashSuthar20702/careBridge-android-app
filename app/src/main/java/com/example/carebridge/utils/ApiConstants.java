// File: ApiConstants.java
package com.example.carebridge.utils;

public class ApiConstants {

    // Flag to use localhost (simulator) or real device/server
    public static final boolean USE_LOCALHOST = true;

    // Localhost IP (emulator uses 10.0.2.2, real device typically uses your network/server address)
    private static final String LOCALHOST_IP = "10.0.2.2";
    private static final String DEVICE_SERVER_IP = "YOUR_REAL_SERVER_IP_OR_DOMAIN";

    // Choose which host to use based on the flag!
    public static String getBaseHost() {
        return USE_LOCALHOST ? LOCALHOST_IP : DEVICE_SERVER_IP;
    }

    // Centralized API root URLs
    private static final String API_ROOT = "/CareBridge/careBridge-web-app/careBridge-website/endpoints/";

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

    // Centralized API end-points
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

}
