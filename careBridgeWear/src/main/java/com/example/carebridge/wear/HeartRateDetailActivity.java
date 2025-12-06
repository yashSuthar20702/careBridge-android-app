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
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHeartRateDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String heartRateValue = getIntent().getStringExtra("metric_value");
        if (heartRateValue != null) {
            binding.heartRateValue.setText(heartRateValue);
            try {
                int initialRate = Integer.parseInt(heartRateValue);
                // Initialize history with the current value
                for (int i = 0; i < 10; i++) {
                    heartRateHistory.add(initialRate);
                }
            } catch (NumberFormatException e) {
                // Initialize with default values
                for (int i = 0; i < 10; i++) {
                    heartRateHistory.add(70 + random.nextInt(10));
                }
            }
        } else {
            // Initialize with default values
            for (int i = 0; i < 10; i++) {
                heartRateHistory.add(70 + random.nextInt(10));
            }
        }

        setupUI();
        setupSensor();
        updateGraph();
        updateStats(getCurrentHeartRate());
    }

    private void setupUI() {
        binding.heartRateBackButton.setOnClickListener(v -> finish());
        binding.heartRateTitle.setText("Heart Rate");

        binding.startMonitoringButton.setOnClickListener(v -> toggleMonitoring());

        // Set initial button color
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));
    }

    private void setupSensor() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                == PackageManager.PERMISSION_GRANTED) {

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

            if (heartRateSensor != null) {
                // Don't start monitoring immediately, wait for button click
                binding.sensorStatus.setText("Sensor Available");
                binding.sensorIndicator.setVisibility(View.VISIBLE);
                binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            } else {
                binding.sensorStatus.setText("Using Simulated Data");
                binding.sensorIndicator.setVisibility(View.VISIBLE);
                binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                startSimulatedUpdates();
            }
        } else {
            binding.sensorStatus.setText("Using Simulated Data");
            binding.sensorIndicator.setVisibility(View.VISIBLE);
            binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
            startSimulatedUpdates();
        }
    }

    private void toggleMonitoring() {
        if (isMonitoring) {
            stopMonitoring();
        } else {
            startMonitoring();
        }
    }

    private void startMonitoring() {
        isMonitoring = true;
        binding.startMonitoringButton.setText("Stop Monitoring");
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.red));

        if (sensorManager != null && heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            binding.sensorStatus.setText("Monitoring Active");
            binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
        } else {
            // If no sensor, use simulated data
            binding.sensorStatus.setText("Simulated Monitoring");
            binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        }

        // Start pulse animation
        startPulseAnimation();
    }

    private void stopMonitoring() {
        isMonitoring = false;
        binding.startMonitoringButton.setText("Start Monitoring");
        binding.startMonitoringButton.setBackgroundColor(
                ContextCompat.getColor(this, R.color.green));

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        binding.sensorStatus.setText("Sensor Available");
        binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.green));

        // Stop pulse animation
        binding.heartIcon.clearAnimation();
    }

    private void startPulseAnimation() {
        ScaleAnimation pulse = new ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulse.setDuration(600);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        binding.heartIcon.startAnimation(pulse);
    }

    private void startSimulatedUpdates() {
        handler.post(updateRunnable);
    }

    private void updateSimulatedData() {
        int heartRate = 65 + random.nextInt(25); // 65-90 BPM
        binding.heartRateValue.setText(String.valueOf(heartRate));

        // Update history
        heartRateHistory.add(heartRate);
        if (heartRateHistory.size() > 20) {
            heartRateHistory.remove(0);
        }

        updateGraph();
        updateStats(heartRate);
    }

    private void updateGraph() {
        if (binding.graphContainer == null) return;

        binding.graphContainer.removeAllViews();

        int maxHeight = 80; // dp
        int minHeartRate = 60;
        int maxHeartRate = 100;

        for (int i = 0; i < heartRateHistory.size(); i++) {
            int rate = heartRateHistory.get(i);
            float percentage = (float) (rate - minHeartRate) / (maxHeartRate - minHeartRate);
            int height = (int) (maxHeight * percentage);

            // Ensure minimum height
            if (height < 4) height = 4;

            View bar = new View(this);
            bar.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(
                            8, // width in dp
                            height
                    );
            params.setMargins(2, maxHeight - height, 2, 0);

            binding.graphContainer.addView(bar, params);
        }
    }

    private int getCurrentHeartRate() {
        if (!heartRateHistory.isEmpty()) {
            return heartRateHistory.get(heartRateHistory.size() - 1);
        }
        return 72; // Default value
    }

    private void updateStats(int currentRate) {
        if (heartRateHistory.isEmpty()) return;

        int sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int rate : heartRateHistory) {
            sum += rate;
            if (rate < min) min = rate;
            if (rate > max) max = rate;
        }

        int avg = sum / heartRateHistory.size();

        if (binding.avgHeartRate != null) {
            binding.avgHeartRate.setText(String.valueOf(avg));
        }
        if (binding.minHeartRate != null) {
            binding.minHeartRate.setText(String.valueOf(min));
        }
        if (binding.maxHeartRate != null) {
            binding.maxHeartRate.setText(String.valueOf(max));
        }

        // Update status
        if (binding.heartRateStatus != null) {
            if (currentRate < 60) {
                binding.heartRateStatus.setText("Low");
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.blue_500));
            } else if (currentRate > 100) {
                binding.heartRateStatus.setText("High");
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                binding.heartRateStatus.setText("Normal");
                binding.heartRateStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values.length > 0) {
            int heartRate = (int) event.values[0];
            runOnUiThread(() -> {
                binding.heartRateValue.setText(String.valueOf(heartRate));

                // Update history
                heartRateHistory.add(heartRate);
                if (heartRateHistory.size() > 20) {
                    heartRateHistory.remove(0);
                }

                updateGraph();
                updateStats(heartRate);
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes
        runOnUiThread(() -> {
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    binding.sensorStatus.setText("High Accuracy");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    binding.sensorStatus.setText("Medium Accuracy");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    binding.sensorStatus.setText("Low Accuracy");
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    binding.sensorStatus.setText("Unreliable");
                    binding.sensorIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
                    break;
            }
        });
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