package com.example.carebridge.wear;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carebridge.wear.databinding.ActivityHealthMonitorBinding;
import com.example.carebridge.wear.utils.HealthDataManager;

import java.util.Locale;

public class HealthMonitorActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "HealthMonitorActivity";
    private static final int REQUEST_SENSOR_PERMISSIONS = 101;

    private ActivityHealthMonitorBinding binding;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor stepCounterSensor;
    private Sensor accelerometerSensor;

    private HealthDataManager healthDataManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isMonitoring = false;
    private boolean isUsingMockData = true; // Track if we're using mock data
    private int pulseAnimationCounter = 0;

    // Default values
    private int currentHeartRate = 72;
    private int currentBloodOxygen = 98;
    private int currentSteps = 3542;
    private int currentCalories = 385;
    private long lastSensorUpdateTime = 0;
    private static final long SENSOR_TIMEOUT = 5000; // 5 seconds without sensor data

    private final Runnable pulseAnimation = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring) {
                pulseAnimationCounter++;
                float scale = 1.0f + (float) Math.sin(pulseAnimationCounter * 0.3) * 0.1f;
                binding.heartIcon.setScaleX(scale);
                binding.heartIcon.setScaleY(scale);
                handler.postDelayed(this, 100);
            }
        }
    };

    private final Runnable mockDataUpdater = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring && isUsingMockData) {
                // Only update if we're not getting sensor data
                long timeSinceLastSensor = System.currentTimeMillis() - lastSensorUpdateTime;

                if (timeSinceLastSensor > SENSOR_TIMEOUT) {
                    // Update heart rate (simulate realistic variation)
                    int heartRateChange = (int) (Math.random() * 5) - 2; // -2 to +2
                    currentHeartRate = Math.max(60, Math.min(100, currentHeartRate + heartRateChange));
                    updateHeartRateUI(currentHeartRate);

                    // Update blood oxygen
                    int spO2Change = (int) (Math.random() * 3) - 1; // -1 to +1
                    currentBloodOxygen = Math.max(95, Math.min(100, currentBloodOxygen + spO2Change));
                    updateBloodOxygenUI(currentBloodOxygen);

                    // Update steps (30% chance to increment)
                    if (Math.random() > 0.7) {
                        int stepsAdded = (int) (Math.random() * 3) + 1;
                        currentSteps += stepsAdded;
                        currentCalories += stepsAdded / 10;
                        updateStepsUI(currentSteps);
                        updateCaloriesUI(currentCalories);
                        updateActivityLevelUI(currentSteps);
                    }

                    Log.d(TAG, "Mock data updated: HR=" + currentHeartRate + ", Steps=" + currentSteps);
                }
            }
            handler.postDelayed(this, 3000); // Update every 3 seconds
        }
    };

    private final Runnable sensorTimeoutChecker = new Runnable() {
        @Override
        public void run() {
            long timeSinceLastSensor = System.currentTimeMillis() - lastSensorUpdateTime;

            if (timeSinceLastSensor > SENSOR_TIMEOUT) {
                // Switch back to mock data if no sensor data for 5 seconds
                isUsingMockData = true;
                Log.d(TAG, "Switching to mock data - no sensor updates");
            }

            handler.postDelayed(this, 1000); // Check every second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        healthDataManager = new HealthDataManager(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        setupUI();
        checkAndRequestPermissions();
    }

    private void setupUI() {
        binding.healthMonitorBackButton.setOnClickListener(v -> finish());

        binding.monitorButton.setOnClickListener(v -> {
            if (!isMonitoring) {
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });

        // Initialize with saved data or defaults
        currentHeartRate = healthDataManager.getLastHeartRate();
        currentSteps = healthDataManager.getLastSteps();

        updateHeartRateUI(currentHeartRate);
        updateBloodOxygenUI(currentBloodOxygen);
        updateStepsUI(currentSteps);
        updateCaloriesUI(currentCalories);
        updateActivityLevelUI(currentSteps);
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACTIVITY_RECOGNITION
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_SENSOR_PERMISSIONS);
        } else {
            setupSensors();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SENSOR_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                setupSensors();
                Toast.makeText(this, "Health monitoring enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some features require sensor permissions", Toast.LENGTH_LONG).show();
                startMockDataOnly();
            }
        }
    }

    private void setupSensors() {
        // Heart Rate Sensor
        if (sensorManager != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            if (heartRateSensor != null) {
                Log.d(TAG, "Heart rate sensor available: " + heartRateSensor.getName());
            } else {
                Log.w(TAG, "Heart rate sensor not available");
            }

            // Step Counter Sensor
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor != null) {
                Log.d(TAG, "Step counter sensor available: " + stepCounterSensor.getName());
            } else {
                Log.w(TAG, "Step counter sensor not available");
            }

            // Accelerometer for activity detection
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor != null) {
                Log.d(TAG, "Accelerometer sensor available: " + accelerometerSensor.getName());
            }
        }

        // Start mock data updates for demonstration
        startMockDataOnly();
    }

    private void startMockDataOnly() {
        handler.post(mockDataUpdater);
        handler.post(sensorTimeoutChecker);
        isUsingMockData = true;
    }

    private void startMonitoring() {
        isMonitoring = true;
        binding.monitorButton.setText("Stop Monitoring");
        binding.monitorButton.setBackgroundResource(R.drawable.button_red);
        binding.sensorStatus.setVisibility(View.VISIBLE);
        binding.heartRateValue.setTextColor(getResources().getColor(R.color.red_400, getTheme()));

        // Register sensor listeners if available
        if (sensorManager != null) {
            if (heartRateSensor != null) {
                sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Heart rate sensor registered");
            }
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                Log.d(TAG, "Step counter sensor registered");
            }
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Accelerometer sensor registered");
            }
        }

        // Start pulse animation
        handler.post(pulseAnimation);

        // Start timeout checker
        handler.post(sensorTimeoutChecker);

        Toast.makeText(this, "Health monitoring started", Toast.LENGTH_SHORT).show();
    }

    private void stopMonitoring() {
        isMonitoring = false;
        binding.monitorButton.setText("Start Monitoring");
        binding.monitorButton.setBackgroundResource(R.drawable.button_green);
        binding.sensorStatus.setVisibility(View.GONE);
        binding.heartRateValue.setTextColor(getResources().getColor(R.color.white, getTheme()));

        // Unregister sensor listeners
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Sensors unregistered");
        }

        // Reset animation
        binding.heartIcon.setScaleX(1.0f);
        binding.heartIcon.setScaleY(1.0f);

        // Stop all handlers
        handler.removeCallbacks(pulseAnimation);
        handler.removeCallbacks(mockDataUpdater);
        handler.removeCallbacks(sensorTimeoutChecker);

        Toast.makeText(this, "Health monitoring stopped", Toast.LENGTH_SHORT).show();
    }

    private void updateHeartRateUI(int heartRate) {
        runOnUiThread(() -> {
            binding.heartRateValue.setText(heartRate + " BPM");

            // Update status based on heart rate
            String status;
            int colorRes;
            if (heartRate < 60) {
                status = "Low";
                colorRes = R.color.blue_400;
            } else if (heartRate > 100) {
                status = "High";
                colorRes = R.color.red_400;
            } else {
                status = "Normal";
                colorRes = R.color.green_400;
            }

            binding.heartRateStatus.setText(status);
            binding.heartRateStatus.setTextColor(getResources().getColor(colorRes, getTheme()));

            // Save to health data manager
            healthDataManager.saveHeartRate(heartRate);
        });
    }

    private void updateBloodOxygenUI(int spO2) {
        runOnUiThread(() -> {
            binding.bloodOxygenValue.setText(spO2 + "%");
        });
    }

    private void updateStepsUI(int steps) {
        runOnUiThread(() -> {
            binding.stepsValue.setText(String.format(Locale.getDefault(), "%,d", steps));
            healthDataManager.saveSteps(steps);
        });
    }

    private void updateCaloriesUI(int calories) {
        runOnUiThread(() -> {
            binding.caloriesValue.setText(String.valueOf(calories));
        });
    }

    private void updateActivityLevelUI(int steps) {
        runOnUiThread(() -> {
            String activityLevel;
            int colorRes;

            if (steps > 5000) {
                activityLevel = "High";
                colorRes = R.color.green_500;
            } else if (steps > 2000) {
                activityLevel = "Medium";
                colorRes = R.color.yellow_500;
            } else {
                activityLevel = "Low";
                colorRes = R.color.gray_400;
            }

            binding.activityLevelValue.setText(activityLevel);
            binding.activityLevelValue.setTextColor(getResources().getColor(colorRes, getTheme()));
        });
    }

    // Sensor Event Listeners
    @Override
    public void onSensorChanged(SensorEvent event) {
        lastSensorUpdateTime = System.currentTimeMillis();
        isUsingMockData = false; // We're getting real sensor data

        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                float heartRate = event.values[0];
                if (heartRate > 0) {
                    currentHeartRate = (int) heartRate;
                    updateHeartRateUI(currentHeartRate);
                    Log.d(TAG, "Real sensor: Heart rate = " + heartRate + " BPM");

                    // Show sensor source indicator
                    runOnUiThread(() -> {
                        binding.sensorStatus.setVisibility(View.VISIBLE);
                        binding.sensorStatus.findViewById(R.id.sensor_status_text).setVisibility(View.VISIBLE);
                    });
                }
                break;

            case Sensor.TYPE_STEP_COUNTER:
                float steps = event.values[0];
                currentSteps = (int) steps;
                updateStepsUI(currentSteps);
                Log.d(TAG, "Real sensor: Step count = " + steps);
                break;

            case Sensor.TYPE_ACCELEROMETER:
                // Calculate activity level based on acceleration
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x*x + y*y + z*z);
                if (acceleration > 15) {
                    runOnUiThread(() -> {
                        binding.activityLevelValue.setText("Active");
                        binding.activityLevelValue.setTextColor(getResources().getColor(R.color.green_500, getTheme()));
                    });
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Sensor accuracy changed: " + sensor.getName() + " = " + accuracy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMonitoring) {
            stopMonitoring();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Don't auto-start monitoring on resume
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}