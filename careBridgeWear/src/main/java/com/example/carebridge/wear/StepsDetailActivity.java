package com.example.carebridge.wear;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.wear.databinding.ActivityStepsDetailBinding;

import java.util.Random;

public class StepsDetailActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityStepsDetailBinding binding;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private int dailyGoal = 10000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateSimulatedSteps();
            handler.postDelayed(this, 2000); // Update every 2 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStepsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String stepsValue = getIntent().getStringExtra("metric_value");
        if (stepsValue != null) {
            try {
                stepCount = Integer.parseInt(stepsValue);
            } catch (NumberFormatException e) {
                stepCount = 3542;
            }
        }

        setupUI();
        setupSensor();
        updateDisplay();
    }

    private void setupUI() {
        binding.stepsBackButton.setOnClickListener(v -> finish());
        binding.stepsTitle.setText(getString(R.string.steps_tracker_title));

        // Set goal
        binding.stepsGoal.setText(String.valueOf(dailyGoal));
    }

    private void setupSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // Start simulated updates if no sensor
            handler.post(updateRunnable);
        }
    }

    private void updateSimulatedSteps() {
        // Simulate step increase
        if (Math.random() > 0.7) {
            stepCount += 1 + random.nextInt(5);
            updateDisplay();
        }
    }

    private void updateDisplay() {
        binding.stepsValue.setText(String.valueOf(stepCount));

        // Calculate progress
        float progress = Math.min((float) stepCount / dailyGoal, 1.0f);
        int progressPercent = (int) (progress * 100);

        binding.stepsProgress.setText(progressPercent + "%");

        // Update progress bar
        binding.progressBar.setProgress(progressPercent);

        // Calculate and display stats
        calculateStats();
    }

    private void calculateStats() {
        // Distance in km (average step length 0.0008 km)
        float distance = stepCount * 0.0008f;
        binding.distanceValue.setText(String.format("%.2f km", distance));

        // Calories burned (approx 0.04 calories per step)
        int calories = (int) (stepCount * 0.04);
        binding.caloriesValue.setText(calories + " cal");

        // Active minutes (approx 1 minute per 100 steps)
        int activeMinutes = stepCount / 100;
        binding.activeMinutesValue.setText(activeMinutes + " min");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && event.values.length > 0) {
            int newStepCount = (int) event.values[0];
            if (newStepCount > stepCount) {
                stepCount = newStepCount;
                runOnUiThread(this::updateDisplay);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
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