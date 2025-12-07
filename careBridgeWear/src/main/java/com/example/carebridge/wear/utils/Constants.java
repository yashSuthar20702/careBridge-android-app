package com.example.carebridge.wear.utils;

/**
 * Constants file for Wear OS application.
 * Centralizes all string and configuration values for maintainability.
 */
public class Constants {

    // HTTP Constants
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HTTP_CONTENT_TYPE_JSON = "application/json";
    public static final String HTTP_CONTENT_TYPE_JSON_CHARSET = "application/json; charset=utf-8";

    // Permission Request Codes
    public static final int PERMISSIONS_REQUEST_BODY_SENSORS = 100;
    public static final int PERMISSIONS_REQUEST_NOTIFICATIONS = 200;

    // User keys
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "wear_os_fcm_token";

    // Time/Date Constants
    public static final String TIME_FORMAT_HH_MM_A = "hh:mm a";
    public static final String TIMEZONE_TORONTO = "America/Toronto";

    // Update Intervals
    public static final int UPDATE_INTERVAL_FAST = 1000; // 1 second
    public static final int UPDATE_INTERVAL_MEDIUM = 3000; // 3 seconds
    public static final int UPDATE_INTERVAL_SLOW = 5000; // 5 seconds

    // Intent/Argument Keys
    public static final String EXTRA_USER = "user";

    // Logging Tags
    public static final String TAG_LOGIN_ACTIVITY = "WearLoginActivity";
    public static final String TAG_CALL_ACTIVITY = "CallActivity";

    // Emoji Logging Constants
    public static final String LOG_EMOJI_SUCCESS = "🟢";
    public static final String LOG_EMOJI_ERROR = "❌";
    public static final String LOG_EMOJI_UPDATE = "🔄";
    public static final String LOG_EMOJI_INFO = "📌";
    public static final String LOG_EMOJI_CLICK = "👉";
    public static final String LOG_EMOJI_CALL = "📞";
    public static final String LOG_EMOJI_LONG_CLICK = "🔍";
    public static final String LOG_EMOJI_BIND = "🔗";

    // URI Schemes
    public static final String URI_SCHEME_TEL = "tel:";

    // Empty/Fallback Values
    public static final String EMPTY_STRING = "";
    public static final String VALUE_UNKNOWN = "Unknown";
    public static final String VALUE_NOT_AVAILABLE = "Not Available";
    public static final String VALUE_NO_DATA = "No Data Available";

    // String Constants
    public static final String SPACE = " ";
    public static final String COLON = ":";
    public static final String VERTICAL_BAR = "|";

    // Log Message Constants
    public static final String LOG_MSG_ACTIVITY_STARTED = "CallActivity started";
    public static final String LOG_MSG_EMERGENCY_CLICKED = "Emergency clicked";
    public static final String LOG_MSG_FETCHING_GUARDIANS = "Fetching guardians...";
    public static final String LOG_MSG_API_SUCCESS = "API Success → Count";
    public static final String LOG_MSG_API_FAILED = "API Failed";
    public static final String LOG_MSG_LOADED_GUARDIAN = "Loaded Guardian →";
    public static final String LOG_MSG_SAMPLE_DATA_LOADED = "Sample data loaded.";
    public static final String LOG_MSG_LOADING = "Loading...";
    public static final String LOG_MSG_CALL_CLICK = "Call Click →";
    public static final String LOG_MSG_DIAL_LAUNCHED = "Dial Intent Launched";
    public static final String LOG_MSG_CALL_FAILED = "Call failed";
    public static final String LOG_MSG_FCM_TOKEN = "FCM Token (Wear): ";
    public static final String LOG_MSG_TOKEN_FETCH_FAILED = "FCM token fetch failed";
    public static final String LOG_MSG_TOKEN_UPDATE_RESPONSE = "Wear FCM token update response: ";
    public static final String LOG_MSG_TOKEN_UPDATE_FAILED = "Wear failed to update FCM token: ";
    public static final String LOG_MSG_TOKEN_JSON_ERROR = "FCM token JSON building error: ";

    // Sample Data Constants
    public static final String SAMPLE_NAME_YASH = "Yash";
    public static final String SAMPLE_NAME_DHWANI = "Dhwani";
    public static final String SAMPLE_NAME_JASJIT = "Jasjit S";
    public static final String SAMPLE_NAME_DR_SMITH = "Dr. Smith";
    public static final String SAMPLE_TYPE_FAMILY = "Family";
    public static final String SAMPLE_TYPE_CARETAKER = "Caretaker";
    public static final String SAMPLE_TYPE_MEDICAL = "Medical";
    public static final String SAMPLE_RELATION_FRIEND = "Friend";
    public static final String SAMPLE_RELATION_NURSE = "Primary Nurse";
    public static final String SAMPLE_RELATION_GUARDIAN = "Primary Guardian";
    public static final String SAMPLE_RELATION_DOCTOR = "Primary Doctor";
    public static final String SAMPLE_PHONE_YASH = "+1 519-569-2560";
    public static final String SAMPLE_PHONE_DHWANI = "+1 519-568-2540";
    public static final String SAMPLE_PHONE_JASJIT = "+1 519-573-0317";
    public static final String SAMPLE_PHONE_DR_SMITH = "+1 519-555-1234";

    // Layout & UI Constants
    public static final int HOME_PAGER_COUNT = 6; // Call, Medicine, Patient Health, Guardian Info, Health Monitor, Logout
    public static final int HEALTH_METRICS_COUNT = 3; // Heart Rate, Steps, Blood Oxygen
    public static final int HEALTH_HISTORY_LIMIT = 100; // Max stored heart rate readings
    public static final int STEPS_HISTORY_LIMIT = 50; // Max stored steps readings
    public static final int GRAPH_READINGS_COUNT = 10; // Number of readings shown in graph

    // Animation Constants
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 250;
    public static final int ANIMATION_DURATION_LONG = 600;
    public static final float SCALE_ACTIVE = 1.25f;
    public static final float SCALE_INACTIVE = 1.0f;

    // Indicator Constants
    public static final int INDICATOR_MARGIN_ACTIVE = 16; // dp
    public static final int INDICATOR_MARGIN_INACTIVE = 8; // dp

    // Swipe Gesture Constants
    public static final int SWIPE_THRESHOLD = 80; // Increased threshold for Wear OS
    public static final int SWIPE_VELOCITY_THRESHOLD = 80;
    public static final int SWIPE_MAX_OFF_PATH = 200; // Allow some vertical movement

    // Time & Update Intervals
    public static final int ONE_DAY_MS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    // Health Default Values
    public static final int DEFAULT_HEART_RATE = 72; // BPM
    public static final int DEFAULT_STEPS = 0;
    public static final int DEFAULT_BLOOD_OXYGEN = 98; // Percentage
    public static final int HEART_RATE_MIN = 65;
    public static final int HEART_RATE_MAX = 90;
    public static final int BLOOD_OXYGEN_MIN = 95;
    public static final int BLOOD_OXYGEN_MAX = 100;

    // Health Calculations
    public static final float STEP_LENGTH_KM = 0.0008f; // Average step length in km
    public static final float CALORIES_PER_STEP = 0.04f; // Calories burned per step
    public static final int STEPS_PER_MINUTE = 100; // Steps per active minute

    // UI Opacity Values
    public static final float OPACITY_SELECTED = 1.0f;
    public static final float OPACITY_UNSELECTED = 0.8f;
    public static final float OPACITY_ICON_SELECTED = 1.0f;
    public static final float OPACITY_ICON_UNSELECTED = 0.5f;
    public static final float OPACITY_TINT = 0.6f;
    public static final float OPACITY_FULL = 1f;
    public static final float OPACITY_DIM = 0.6f;

    // Color Alpha Adjustments
    public static final float ALPHA_SELECTED = 0.9f;
    public static final float ALPHA_UNSELECTED = 0.4f;

    // Gradient Constants
    public static final float GRADIENT_CORNER_RADIUS = 16f;

    // Graph Constants
    public static final int GRAPH_BAR_WIDTH = 12; // dp
    public static final int GRAPH_BAR_MARGIN = 2; // dp
    public static final int GRAPH_MAX_HEIGHT = 80; // dp

    // Position Constants
    public static final int POSITION_INVALID = -1;
    public static final int POSITION_FIRST = 0;

    // Button Index Constants (Home Pager)
    public static final int BUTTON_CALL = 0;
    public static final int BUTTON_MEDICINE = 1;
    public static final int BUTTON_PATIENT_HEALTH = 2;
    public static final int BUTTON_GUARDIAN_INFO = 3;
    public static final int BUTTON_HEALTH_MONITOR = 4;
    public static final int BUTTON_LOGOUT = 5;

    // Date/Time Formats
    public static final String TIME_FORMAT_HH_MM = "HH:mm";

    // Shared Preferences Constants
    public static final int PREF_MODE = 0; // Context.MODE_PRIVATE value

    // Work Manager Constants
    public static final String WORKER_INPUT_LOG_ID = "log_id";
    public static final String WORKER_INPUT_STATUS = "taken_status";

    // API Configuration
    public static final String BASE_URL = "http://10.0.2.2/careBridge/careBridge-web-app/careBridge-website/endpoints/medicine_log/";
    public static final String UPDATE_STATUS_ENDPOINT = "updateStatus.php";
    public static final String MEDIA_TYPE_JSON = "application/json; charset=utf-8";

    // Health Metric IDs
    public static final String METRIC_HEART_RATE = "heart_rate";
    public static final String METRIC_STEPS = "steps";
    public static final String METRIC_BLOOD_OXYGEN = "blood_oxygen";

    // FCM Configuration
    public static final String FCM_CHANNEL_ID = "carebridge_wear_channel";
    public static final String FCM_CHANNEL_NAME = "Wear Medication Alerts";
    public static final String FCM_MEDICINE_TITLE = "💊 Medicine Reminder";
    public static final String FCM_MEDICINE_MESSAGE = "Tap to confirm";

    // FCM Actions
    public static final String ACTION_MED_TAKEN = "WEAR_MED_TAKEN";
    public static final String ACTION_MED_NOT_TAKEN = "WEAR_MED_NOT_TAKEN";
    public static final String EXTRA_LOG_ID = "log_id";
    public static final String EXTRA_TAKEN_STATUS = "taken_status";

    // Medicine Status
    public static final String STATUS_TAKEN = "Taken";
    public static final String STATUS_NOT_TAKEN = "Not Taken";

    // Logging Tags
    public static final String TAG_GUARDIAN_ACTIVITY = "GuardianActivity";

    // Log Message Constants
    public static final String LOG_MSG_GUARDIAN_FETCHING_STARTED = "Starting to fetch guardians data for GuardianActivity...";
    public static final String LOG_MSG_GUARDIAN_FETCH_SUCCESS = "Guardians data fetched successfully. Count";
    public static final String LOG_MSG_GUARDIAN_FETCH_FAILED = "Failed to fetch guardians data";
    public static final String LOG_MSG_GUARDIAN_NO_DATA = "No guardians found in API response";
    public static final String LOG_MSG_GUARDIAN_EMPTY_STATE = "No guardians available to display";
    public static final String LOG_MSG_GUARDIAN_ERROR_STATE = "Error state";

    // Logging Tags
    public static final String TAG_HEALTH_INFO_ACTIVITY = "HealthInfoActivity";

    // String Constants
    public static final String COMMA_SPACE = ", ";

    // DOB Calculation Constants
    public static final int DOB_YEAR_LENGTH = 4;

    // Log Message Constants
    public static final String LOG_MSG_HEALTH_INFO_FETCHING = "Starting to fetch patient data...";
    public static final String LOG_MSG_HEALTH_INFO_FETCH_SUCCESS = "Patient data fetched successfully";
    public static final String LOG_MSG_HEALTH_INFO_FETCH_FAILED = "Failed to fetch patient data";
    public static final String LOG_MSG_HEALTH_INFO_AGE_CALC_ERROR = "Error calculating age from DOB";
    public static final String LOG_MSG_HEALTH_INFO_ERROR_STATE = "Error state";

    // Sample Data Constants
    public static final String SAMPLE_BLOOD_GROUP = "B+";
    public static final String SAMPLE_AGE = "29";
    public static final String SAMPLE_ADDRESS = "123 Oak Street, Springfield";

    // Logging Tags
    public static final String TAG_HEALTH_MONITOR_ACTIVITY = "HealthMonitorActivity";

    // Log Message Constants
    public static final String LOG_MSG_HEART_RATE_SENSOR_REGISTERED = "Heart rate sensor registered";
    public static final String LOG_MSG_NO_HEART_RATE_SENSOR = "No heart rate sensor available";
    public static final String LOG_MSG_STEP_COUNTER_SENSOR_REGISTERED = "Step counter sensor registered";
    public static final String LOG_MSG_NO_STEP_COUNTER_SENSOR = "No step counter sensor available";
    public static final String LOG_MSG_SENSOR_ACCURACY_CHANGED = "Sensor accuracy changed";
    public static final String LOG_MSG_ACCURACY = "accuracy";

    // Class Names for Intent
    public static final String CLASS_HEART_RATE_DETAIL = "HeartRateDetailActivity";
    public static final String CLASS_STEPS_DETAIL = "StepsDetailActivity";
    public static final String CLASS_BLOOD_OXYGEN_DETAIL = "BloodOxygenDetailActivity";

    // Float Constants
    public static final float FLOAT_DIVISOR_TWO = 2f;
    public static final float FLOAT_SCALE_BASE = 1.0f;
    public static final float FLOAT_SCALE_FACTOR = 0.15f;
    public static final float FLOAT_SCALE_MIN = 0.85f;
    public static final float FLOAT_SCALE_MAX = 1.1f;
    public static final float FLOAT_ALPHA_BASE = 1.0f;
    public static final float FLOAT_ALPHA_FACTOR = 0.3f;
    public static final float FLOAT_ALPHA_MIN = 0.7f;
    public static final float FLOAT_ALPHA_MAX = 1.0f;
    public static final float FLOAT_ALPHA_START = 0f;
    public static final float FLOAT_ALPHA_END = 1f;

    // Health Simulation Constants
    public static final int DEFAULT_STEPS_VALUE = 3542;
    public static final int HEART_RATE_RANGE = 20;
    public static final int STEPS_BASE_VALUE = 3500;
    public static final int STEPS_RANGE = 500;
    public static final int BLOOD_OXYGEN_RANGE = 4;




    public static final String EXTRA_METRIC_VALUE = "metric_value";
    public static final String EXTRA_METRIC_LABEL = "metric_label";

    // Heart Rate Activity Constants
    public static final int INITIAL_HISTORY_SIZE = 10;
    public static final int DEFAULT_HEART_RATE_HISTORY_BASE = 70;
    public static final int DEFAULT_HEART_RATE_HISTORY_RANGE = 10;
    public static final int MAX_HISTORY_SIZE = 20;
    public static final int INDEX_LAST_ELEMENT = 1;
    public static final int HEART_RATE_SIM_MIN = 65;
    public static final int HEART_RATE_SIM_RANGE = 25;
    public static final int HEART_RATE_LOW_THRESHOLD = 60;
    public static final int HEART_RATE_HIGH_THRESHOLD = 100;

    // Float Constants
    public static final float FLOAT_SCALE_START = 1.0f;
    public static final float FLOAT_SCALE_END = 1.2f;
    public static final float FLOAT_CENTER_POSITION = 0.5f;

    // Steps Activity Constants
    public static final int DAILY_STEP_GOAL = 10000;

    public static final float STEP_UPDATE_PROBABILITY = 0.7f;
    public static final int STEP_INCREMENT_BASE = 1;
    public static final int STEP_INCREMENT_RANGE = 5;


    // Units and Formats
    public static final String UNIT_CALORIES = "cal";
    public static final String UNIT_MINUTES = "min";
    public static final String DISTANCE_FORMAT = "%.2f km";
    // FCM Service Constants
    public static final String TAG_FCM_SERVICE = "WearFCMService";
    public static final String FCM_KEY_TITLE = "title";
    public static final String FCM_KEY_BODY = "body";
    public static final String FCM_KEY_LOG_ID = "log_id";

    // Log Message Constants
    public static final String LOG_MSG_FCM_DATA_PAYLOAD = "Wear Data Payload";
    public static final String LOG_MSG_INVALID_LOG_ID = "Invalid log ID";

    // Notification Constants
    public static final int DEFAULT_NOTIFICATION_ID = 0;
    public static final int PENDING_INTENT_TAKEN_OFFSET = 1;
    public static final int PENDING_INTENT_NOT_TAKEN_OFFSET = 2;
    public static final long[] NOTIFICATION_VIBRATION_PATTERN = {200, 200};
    public static final String NOTIFICATION_ACTION_TAKEN = "Taken";
    public static final String NOTIFICATION_ACTION_NOT_TAKEN = "Not Taken";

    // Log Message Constants
    public static final String LOG_MSG_NULL_INTENT_RECEIVED = "Received null intent";
    public static final String LOG_MSG_MISSING_INTENT_DATA = "Missing log_id or taken_status in intent";
    public static final String LOG_MSG_RECEIVED_ACTION = "Received action";
    public static final String LOG_MSG_LOG_ID = "logId";
    public static final String LOG_MSG_STATUS = "status";
    public static final String LOG_MSG_INVALID_NOTIFICATION_ID = "Invalid logId for notification cancel";
    public static final String LOG_MSG_STATUS_UPDATE_SUCCESS = "Status updated successfully";
    public static final String LOG_MSG_STATUS_UPDATE_FAILED = "Failed to update status";
    public static final String LOG_MSG_API_CALL_FAILED = "API call failed for";
    public static final String LOG_MSG_WORKMANAGER_ENQUEUED = "Fallback WorkManager enqueued for";

    // String Constants
    public static final String EQUALS = "=";

    // Notification Receiver Tag
    public static final String TAG_NOTIFICATION_RECEIVER = "WearNotificationReceiver";

    public static final String MEDICINE_STATUS_PREFIX = "Medicine";

    // Worker Constants
    public static final String TAG_SYNC_WORKER = "WearSyncWorker";

    // Log Message Constants
    public static final String LOG_MSG_WORKER_MISSING_DATA = "Missing log_id or taken_status";
    public static final String LOG_MSG_WORKER_SYNC_START = "Starting sync for";
    public static final String LOG_MSG_WORKER_UPDATE_SUCCESS = "Successfully updated status for";
    public static final String LOG_MSG_WORKER_UPDATE_FAILED = "Failed to update status for";
    public static final String LOG_MSG_WORKER_API_FAILED = "API call failed for";
    public static final String LOG_MSG_CODE = "Code";

    // Intent/Argument Keys
    public static final String ARG_POSITION = "position";
    // Float Constants
    public static final float FLOAT_SCALE_INACTIVE = 1.0f;
    public static final float FLOAT_SCALE_ACTIVE = 1.25f;




}