package com.example.carebridge.wear;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.carebridge.wear.databinding.ActivityHeartRateDetailBinding;
import com.example.carebridge.wear.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeartRateDetailActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityHeartRateDetailBinding binding;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private boolean isMonitoring = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private List<Integer> heartRateHistory = new ArrayList<>();

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isMonitoring) {
                updateSimulatedData();
            }
            handler.postDelayed(this, Constants.UPDATE_INTERVAL_FAST);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHeartRateDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String heartRateValue = getIntent().getStringExtra(Constants.EXTRA_METRIC_VALUE);
        initializeHeartRateHistory(heartRateValue);

        setupUI();
        setupSensor();
        updateStats(getCurrentHeartRate());
    }

    /**
     * Initialize heart rate history with initial value
     */
    private void initializeHeartRateHistory(String heartRateValue) {
        if (heartRateValue != null) {
            binding.heartRateValue.setText(heartRateValue);
            try {
                int initialRate = Integer.parseInt(heartRateValue);
                // Initialize history with the current value
                for (int i = Constants.POSITION_FIRST; i < Constants.INITIAL_HISTORY_SIZE; i++) {
                    heartRateHistory.add(initialRate);
                }
            } catch (NumberFormatException e) {
                initializeDefaultHistory();
            }
        } else {
            initializeDefaultHistory();
        }
    }

    /**
     * Initialize with default heart rate values
     */
    private void initializeDefaultHistory() {
        for (int i = Constants.POSITION_FIRST; i < Constants.INITIAL_HISTORY_SIZE; i++) {
            heartRateHistory.add(Constants.DEFAULT_HEART_RATE_HISTORY_BASE + random.nextInt(Constants.DEFAULT_HEART_RATE_HISTORY_RANGE));
        }
    }

    /**
     * Set up UI components
     */
    private void setupUI() {
        binding.heartRateTitle.setText(getString(R.string.heart_rate_title));

        binding.startMonitoringButton.setOnClickListener(v -> toggleMonitoring());

        // Set initial button color
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));
    }

    /**
     * Set up sensor for heart rate monitoring
     */
    private void setupSensor() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                == PackageManager.PERMISSION_GRANTED) {

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

            if (heartRateSensor != null) {
                // Don't start monitoring immediately, wait for button click
                binding.sensorStatus.setText(getString(R.string.sensor_available));
                binding.sensorIndicator.setVisibility(View.VISIBLE);
                binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            } else {
                handleNoSensorAvailable();
            }
        } else {
            handleNoSensorAvailable();
        }
    }

    /**
     * Handle case when no sensor is available
     */
    private void handleNoSensorAvailable() {
        binding.sensorStatus.setText(getString(R.string.using_simulated_data));
        binding.sensorIndicator.setVisibility(View.VISIBLE);
        binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        startSimulatedUpdates();
    }

    /**
     * Toggle monitoring state
     */
    private void toggleMonitoring() {
        if (isMonitoring) {
            stopMonitoring();
        } else {
            startMonitoring();
        }
    }

    /**
     * Start heart rate monitoring
     */
    private void startMonitoring() {
        isMonitoring = true;
        binding.startMonitoringButton.setText(getString(R.string.stop_monitoring));
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.red));

        if (sensorManager != null && heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            binding.sensorStatus.setText(getString(R.string.monitoring_active));
            binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            // If no sensor, use simulated data
            binding.sensorStatus.setText(getString(R.string.simulated_monitoring));
            binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        }

        // Start pulse animation
        startPulseAnimation();
    }

    /**
     * Stop heart rate monitoring
     */
    private void stopMonitoring() {
        isMonitoring = false;
        binding.startMonitoringButton.setText(getString(R.string.start_monitoring));
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        binding.sensorStatus.setText(getString(R.string.sensor_available));
        binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        // Stop pulse animation
        binding.heartIcon.clearAnimation();
    }

    /**
     * Start pulse animation for heart icon
     */
    private void startPulseAnimation() {
        ScaleAnimation pulse = new ScaleAnimation(
                Constants.FLOAT_SCALE_START,
                Constants.FLOAT_SCALE_END,
                Constants.FLOAT_SCALE_START,
                Constants.FLOAT_SCALE_END,
                Animation.RELATIVE_TO_SELF,
                Constants.FLOAT_CENTER_POSITION,
                Animation.RELATIVE_TO_SELF,
                Constants.FLOAT_CENTER_POSITION
        );
        pulse.setDuration(Constants.ANIMATION_DURATION_LONG);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        binding.heartIcon.startAnimation(pulse);
    }

    /**
     * Start simulated data updates
     */
    private void startSimulatedUpdates() {
        handler.post(updateRunnable);
    }

    /**
     * Update simulated heart rate data
     */
    private void updateSimulatedData() {
        int heartRate = Constants.HEART_RATE_SIM_MIN + random.nextInt(Constants.HEART_RATE_SIM_RANGE);
        binding.heartRateValue.setText(String.valueOf(heartRate));

        // Update history for statistics calculation
        heartRateHistory.add(heartRate);
        if (heartRateHistory.size() > Constants.MAX_HISTORY_SIZE) {
            heartRateHistory.remove(Constants.POSITION_FIRST);
        }

        updateStats(heartRate);
    }

    /**
     * Get current heart rate from history
     */
    private int getCurrentHeartRate() {
        if (!heartRateHistory.isEmpty()) {
            return heartRateHistory.get(heartRateHistory.size() - Constants.INDEX_LAST_ELEMENT);
        }
        return Constants.DEFAULT_HEART_RATE;
    }

    /**
     * Update statistics display
     */
    private void updateStats(int currentRate) {
        if (heartRateHistory.isEmpty()) return;

        int sum = Constants.POSITION_FIRST;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int rate : heartRateHistory) {
            sum += rate;
            if (rate < min) min = rate;
            if (rate > max) max = rate;
        }

        int avg = sum / heartRateHistory.size();

        // Update status
        updateHeartRateStatus(currentRate);
    }

    /**
     * Update heart rate status display
     */
    private void updateHeartRateStatus(int currentRate) {
        if (binding.heartRateStatus != null) {
            if (currentRate < Constants.HEART_RATE_LOW_THRESHOLD) {
                binding.heartRateStatus.setText(getString(R.string.heart_rate_low));
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.blue_500));
            } else if (currentRate > Constants.HEART_RATE_HIGH_THRESHOLD) {
                binding.heartRateStatus.setText(getString(R.string.heart_rate_high));
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                binding.heartRateStatus.setText(getString(R.string.heart_rate_normal));
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values.length > Constants.POSITION_FIRST) {
            int heartRate = (int) event.values[Constants.POSITION_FIRST];
            runOnUiThread(() -> {
                updateHeartRateFromSensor(heartRate);
            });
        }
    }

    /**
     * Update heart rate from sensor reading
     */
    private void updateHeartRateFromSensor(int heartRate) {
        binding.heartRateValue.setText(String.valueOf(heartRate));

        // Update history for statistics calculation
        heartRateHistory.add(heartRate);
        if (heartRateHistory.size() > Constants.MAX_HISTORY_SIZE) {
            heartRateHistory.remove(Constants.POSITION_FIRST);
        }

        updateStats(heartRate);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes
        runOnUiThread(() -> {
            handleSensorAccuracyChange(accuracy);
        });
    }

    /**
     * Handle sensor accuracy changes
     */
    private void handleSensorAccuracyChange(int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                binding.sensorStatus.setText(getString(R.string.high_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                binding.sensorStatus.setText(getString(R.string.medium_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                binding.sensorStatus.setText(getString(R.string.low_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                binding.sensorStatus.setText(getString(R.string.unreliable));
                binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(updateRunnable);
        if (binding.heartIcon != null) {
            binding.heartIcon.clearAnimation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMonitoring && heartRateSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            startPulseAnimation();
        }
        if (!isMonitoring && heartRateSensor == null) {
            // Only start simulated updates if we're not using real sensor
            handler.post(updateRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacksAndMessages(null);
    }
}