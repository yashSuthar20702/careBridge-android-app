package com.example.carebridge.wear.utils;

/**
 * String resource constants to replace hardcoded strings in Java files.
 * These should match the values in your strings.xml file.
 */
public class StringResources {

    // Button Labels (Home Pager)
    public static final String BUTTON_CALL = "Call";
    public static final String BUTTON_MEDICINE = "Medicine";
    public static final String BUTTON_PATIENT_HEALTH = "Patient Health";
    public static final String BUTTON_GUARDIAN_INFO = "Guardian Info";
    public static final String BUTTON_HEALTH_MONITOR = "Health Monitor";
    public static final String BUTTON_LOGOUT = "Logout";

    // Health Metric Labels
    public static final String HEART_RATE_LABEL = "Heart Rate";
    public static final String STEPS_LABEL = "Steps";
    public static final String BLOOD_OXYGEN_LABEL = "Blood Oxygen";

    // Health Metric Units
    public static final String UNIT_BPM = "BPM";
    public static final String UNIT_STEPS = "steps";
    public static final String UNIT_PERCENT = "%";
    public static final String UNIT_CM = "cm";
    public static final String UNIT_KG = "kg";
    public static final String UNIT_YEARS = "years";

    // Health Metric Descriptions
    public static final String HEART_RATE_DESC = "Resting heart rate";
    public static final String STEPS_DESC = "Steps taken today";
    public static final String BLOOD_OXYGEN_DESC = "Blood oxygen saturation";

    // Activity Titles
    public static final String TITLE_HEALTH_MONITOR = "Health Monitor";
    public static final String TITLE_HEART_RATE = "Heart Rate";
    public static final String TITLE_STEPS_TRACKER = "Steps Tracker";
    public static final String TITLE_BLOOD_OXYGEN = "Blood Oxygen";

    // Error Messages
    public static final String ERROR_NO_INTERNET = "No internet connection";
    public static final String ERROR_NO_MEDICINE = "No medicine found";
    public static final String ERROR_LOAD_FAILED = "Failed to load data";
    public static final String ERROR_NO_GUARDIANS = "No guardians available";
    public static final String ERROR_PREFIX = "Error: ";
    public static final String ERROR_NO_PHONE = "No valid phone number";
    public static final String ERROR_CALL_FAILED = "Call failed";

    // Login Screen
    public static final String LOGIN_ENTER_USERNAME = "Enter username";
    public static final String LOGIN_ENTER_PASSWORD = "Enter password";
    public static final String LOGIN_BUTTON = "Login";
    public static final String LOGIN_LOGGING_IN = "Logging in...";

    // Health Info Labels
    public static final String LABEL_NAME = "Name";
    public static final String LABEL_BLOOD_GROUP = "Blood Group";
    public static final String LABEL_AGE = "Age";
    public static final String LABEL_GENDER = "Gender";
    public static final String LABEL_ADDRESS = "Address";
    public static final String LABEL_CONTACT = "Contact";
    public static final String LABEL_EMAIL = "Email";
    public static final String LABEL_HEIGHT = "Height";
    public static final String LABEL_WEIGHT = "Weight";


    public static final String LABEL_ALLERGIES = "Allergies";
    public static final String LABEL_MEDICAL_CONDITIONS = "Medical Conditions";
    public static final String LABEL_PAST_SURGERIES = "Past Surgeries";
    public static final String LABEL_CURRENT_SYMPTOMS = "Current Symptoms";
    public static final String LABEL_NO_DATA = "No Data";

    // Patient Info
    public static final String PATIENT_INFO_NOT_AVAILABLE = "Patient information not available";

    // Guardian Labels
    public static final String GUARDIAN_TYPE_FAMILY = "Family";
    public static final String GUARDIAN_TYPE_CARETAKER = "Caretaker";
    public static final String GUARDIAN_TYPE_MEDICAL = "Medical";
    public static final String GUARDIAN_ROLE_PRIMARY = "Primary Guardian";
    public static final String GUARDIAN_ROLE_NURSE = "Primary Nurse";
    public static final String GUARDIAN_ROLE_DOCTOR = "Primary Doctor";
    public static final String GUARDIAN_ROLE_CAREGIVER = "Caregiver";

    // Sensor Status Messages
    public static final String SENSOR_AVAILABLE = "Sensor Available";
    public static final String SENSOR_SIMULATED_DATA = "Using Simulated Data";
    public static final String SENSOR_MONITORING_ACTIVE = "Monitoring Active";
    public static final String SENSOR_SIMULATED_MONITORING = "Simulated Monitoring";
    public static final String SENSOR_HIGH_ACCURACY = "High Accuracy";
    public static final String SENSOR_MEDIUM_ACCURACY = "Medium Accuracy";
    public static final String SENSOR_LOW_ACCURACY = "Low Accuracy";
    public static final String SENSOR_UNRELIABLE = "Unreliable";

    // Monitoring Buttons
    public static final String BUTTON_START_MONITORING = "Start Monitoring";
    public static final String BUTTON_STOP_MONITORING = "Stop Monitoring";

    // Health Status Messages
    public static final String HEART_RATE_NORMAL = "Normal";
    public static final String HEART_RATE_LOW = "Low";
    public static final String HEART_RATE_HIGH = "High";

    public static final String BLOOD_OXYGEN_NORMAL = "Normal";
    public static final String BLOOD_OXYGEN_LOW = "Low";
    public static final String BLOOD_OXYGEN_VERY_LOW = "Very Low";
    public static final String BLOOD_OXYGEN_NORMAL_DESC = "Your blood oxygen level is within normal range";
    public static final String BLOOD_OXYGEN_LOW_DESC = "Consider consulting a healthcare provider";
    public static final String BLOOD_OXYGEN_VERY_LOW_DESC = "Seek immediate medical attention";

    // Health Information
    public static final String BLOOD_OXYGEN_NORMAL_RANGE = "Normal Range: 95-100%";
    public static final String BLOOD_OXYGEN_MEASUREMENT_TIP = "Measure on a flat surface with steady hands";

    // Permission Messages
    public static final String PERMISSION_HEALTH_MONITORING = "Health monitoring requires sensor permissions";
    public static final String PERMISSION_GRANT = "Grant";
    public static final String PERMISSION_NOTIFICATIONS = "Notifications are required for medicine reminders";

    // Emergency Contact
    public static final String EMERGENCY_NUMBER = "911";

    // Notification Actions
    public static final String NOTIFICATION_ACTION_TAKEN = "Taken";
    public static final String NOTIFICATION_ACTION_NOT_TAKEN = "Not Taken";

    // Medicine Status
    public static final String MEDICINE_STATUS_TAKEN = "Medicine Taken";
    public static final String MEDICINE_STATUS_NOT_TAKEN = "Medicine Not Taken";
}