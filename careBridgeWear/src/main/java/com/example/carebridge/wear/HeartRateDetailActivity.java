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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.carebridge.wear.databinding.ActivityHeartRateDetailBinding;
import com.example.carebridge.wear.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HeartRateDetailActivity
 *
 * Displays detailed heart-rate information on Wear OS.
 * Supports:
 *  - Real heart-rate sensor monitoring
 *  - Simulated data when sensor is unavailable
 *  - Pulse animation
 *  - Heart-rate status (Low / Normal / High)
 *
 * Fully optimized for small Wear OS screens.
 */
public class HeartRateDetailActivity extends AppCompatActivity
        implements SensorEventListener {

    // ViewBinding for safe UI access
    private ActivityHeartRateDetailBinding binding;

    // Sensor related objects
    private SensorManager sensorManager;
    private Sensor heartRateSensor;

    // Monitoring state
    private boolean isMonitoring = false;

    // Handler for periodic updates (simulation)
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Random generator for simulated values
    private final Random random = new Random();

    // Stores recent heart-rate values for analysis
    private final List<Integer> heartRateHistory = new ArrayList<>();

    // Runnable for simulated heart-rate updates
    private final Runnable updateRunnable = new Runnable() {
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

        // Inflate layout using ViewBinding
        binding = ActivityHeartRateDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get heart-rate value from previous screen
        String heartRateValue =
                getIntent().getStringExtra(Constants.EXTRA_METRIC_VALUE);

        // Initialize history and UI
        initializeHeartRateHistory(heartRateValue);
        setupUI();
        setupSensor();

        // Display initial stats
        updateStats(getCurrentHeartRate());
    }

    /**
     * Initializes heart-rate history using passed value
     */
    private void initializeHeartRateHistory(String heartRateValue) {
        if (heartRateValue != null) {
            binding.heartRateValue.setText(heartRateValue);
            try {
                int initialRate = Integer.parseInt(heartRateValue);
                for (int i = Constants.POSITION_FIRST;
                     i < Constants.INITIAL_HISTORY_SIZE; i++) {
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
     * Initializes default simulated heart-rate history
     */
    private void initializeDefaultHistory() {
        for (int i = Constants.POSITION_FIRST;
             i < Constants.INITIAL_HISTORY_SIZE; i++) {
            heartRateHistory.add(
                    Constants.DEFAULT_HEART_RATE_HISTORY_BASE
                            + random.nextInt(
                            Constants.DEFAULT_HEART_RATE_HISTORY_RANGE));
        }
    }

    /**
     * Sets up UI elements and button actions
     */
    private void setupUI() {
        binding.heartRateTitle.setText(
                getString(R.string.heart_rate_title));

        binding.startMonitoringButton.setOnClickListener(
                v -> toggleMonitoring());

        // Initial button appearance
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));
    }

    /**
     * Prepares heart-rate sensor if permission is granted
     */
    private void setupSensor() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.BODY_SENSORS)
                == PackageManager.PERMISSION_GRANTED) {

            sensorManager =
                    (SensorManager) getSystemService(SENSOR_SERVICE);
            heartRateSensor =
                    sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

            if (heartRateSensor != null) {
                binding.sensorStatus.setText(
                        getString(R.string.sensor_available));
                binding.sensorIndicator.setVisibility(View.VISIBLE);
                binding.sensorIndicator.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.green));
            } else {
                handleNoSensorAvailable();
            }
        } else {
            handleNoSensorAvailable();
        }
    }

    /**
     * Handles case where heart-rate sensor is unavailable
     */
    private void handleNoSensorAvailable() {
        binding.sensorStatus.setText(
                getString(R.string.using_simulated_data));
        binding.sensorIndicator.setVisibility(View.VISIBLE);
        binding.sensorIndicator.setBackgroundColor(
                ContextCompat.getColor(this, R.color.orange));
        startSimulatedUpdates();
    }

    /**
     * Toggles between start and stop monitoring
     */
    private void toggleMonitoring() {
        if (isMonitoring) {
            stopMonitoring();
        } else {
            startMonitoring();
        }
    }

    /**
     * Starts real heart-rate monitoring
     */
    private void startMonitoring() {
        isMonitoring = true;

        binding.startMonitoringButton.setText(
                getString(R.string.stop_monitoring));
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.red));

        if (sensorManager != null && heartRateSensor != null) {
            sensorManager.registerListener(
                    this,
                    heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            binding.sensorStatus.setText(
                    getString(R.string.monitoring_active));
            binding.sensorIndicator.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.red));
        } else {
            binding.sensorStatus.setText(
                    getString(R.string.simulated_monitoring));
            binding.sensorIndicator.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.orange));
        }

        startPulseAnimation();
    }

    /**
     * Stops heart-rate monitoring
     */
    private void stopMonitoring() {
        isMonitoring = false;

        binding.startMonitoringButton.setText(
                getString(R.string.start_monitoring));
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        binding.sensorStatus.setText(
                getString(R.string.sensor_available));
        binding.sensorIndicator.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));

        binding.heartIcon.clearAnimation();
    }

    /**
     * Starts pulse animation on heart icon
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
     * Starts simulated updates when sensor is not used
     */
    private void startSimulatedUpdates() {
        handler.post(updateRunnable);
    }

    /**
     * Generates simulated heart-rate values
     */
    private void updateSimulatedData() {
        int heartRate =
                Constants.HEART_RATE_SIM_MIN
                        + random.nextInt(
                        Constants.HEART_RATE_SIM_RANGE);

        binding.heartRateValue.setText(
                String.valueOf(heartRate));

        heartRateHistory.add(heartRate);
        if (heartRateHistory.size()
                > Constants.MAX_HISTORY_SIZE) {
            heartRateHistory.remove(Constants.POSITION_FIRST);
        }

        updateStats(heartRate);
    }

    /**
     * Returns the most recent heart-rate value
     */
    private int getCurrentHeartRate() {
        if (!heartRateHistory.isEmpty()) {
            return heartRateHistory.get(
                    heartRateHistory.size()
                            - Constants.INDEX_LAST_ELEMENT);
        }
        return Constants.DEFAULT_HEART_RATE;
    }

    /**
     * Updates heart-rate statistics
     */
    private void updateStats(int currentRate) {
        if (heartRateHistory.isEmpty()) return;
        updateHeartRateStatus(currentRate);
    }

    /**
     * Updates heart-rate status text and color
     */
    private void updateHeartRateStatus(int currentRate) {
        if (currentRate
                < Constants.HEART_RATE_LOW_THRESHOLD) {
            binding.heartRateStatus.setText(
                    getString(R.string.heart_rate_low));
            binding.heartRateStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.blue_500));
        } else if (currentRate
                > Constants.HEART_RATE_HIGH_THRESHOLD) {
            binding.heartRateStatus.setText(
                    getString(R.string.heart_rate_high));
            binding.heartRateStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.red));
        } else {
            binding.heartRateStatus.setText(
                    getString(R.string.heart_rate_normal));
            binding.heartRateStatus.setTextColor(
                    ContextCompat.getColor(this, R.color.green));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()
                == Sensor.TYPE_HEART_RATE
                && event.values.length
                > Constants.POSITION_FIRST) {

            int heartRate =
                    (int) event.values[Constants.POSITION_FIRST];
            runOnUiThread(() ->
                    updateHeartRateFromSensor(heartRate));
        }
    }

    /**
     * Updates UI and history using real sensor data
     */
    private void updateHeartRateFromSensor(int heartRate) {
        binding.heartRateValue.setText(
                String.valueOf(heartRate));

        heartRateHistory.add(heartRate);
        if (heartRateHistory.size()
                > Constants.MAX_HISTORY_SIZE) {
            heartRateHistory.remove(Constants.POSITION_FIRST);
        }

        updateStats(heartRate);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        runOnUiThread(() ->
                handleSensorAccuracyChange(accuracy));
    }

    /**
     * Displays sensor accuracy information
     */
    private void handleSensorAccuracyChange(int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                binding.sensorStatus.setText(
                        getString(R.string.high_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                binding.sensorStatus.setText(
                        getString(R.string.medium_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                binding.sensorStatus.setText(
                        getString(R.string.low_accuracy));
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                binding.sensorStatus.setText(
                        getString(R.string.unreliable));
                binding.sensorIndicator.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.orange));
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
        binding.heartIcon.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMonitoring
                && heartRateSensor != null
                && sensorManager != null) {

            sensorManager.registerListener(
                    this,
                    heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            startPulseAnimation();
        }

        if (!isMonitoring && heartRateSensor == null) {
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