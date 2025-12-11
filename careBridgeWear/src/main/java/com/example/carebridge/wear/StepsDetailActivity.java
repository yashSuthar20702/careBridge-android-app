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
import com.example.carebridge.wear.utils.Constants;

import java.util.Random;

public class StepsDetailActivity extends AppCompatActivity implements SensorEventListener {

    private ActivityStepsDetailBinding binding;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = Constants.POSITION_FIRST;
    private int dailyGoal = Constants.DAILY_STEP_GOAL;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateSimulatedSteps();
            handler.postDelayed(this, Constants.UPDATE_INTERVAL_SLOW);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStepsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String stepsValue = getIntent().getStringExtra(Constants.EXTRA_METRIC_VALUE);
        initializeStepCount(stepsValue);

        setupUI();
        setupSensor();
        updateDisplay();
    }

    /**
     * Initialize step count from intent or default value
     */
    private void initializeStepCount(String stepsValue) {
        if (stepsValue != null) {
            try {
                stepCount = Integer.parseInt(stepsValue);
            } catch (NumberFormatException e) {
                stepCount = Constants.DEFAULT_STEPS_VALUE;
            }
        }
    }

    /**
     * Set up UI components
     */
    private void setupUI() {
        binding.stepsTitle.setText(getString(R.string.steps_tracker_title));
    }

    /**
     * Set up step counter sensor
     */
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

    /**
     * Update simulated step data
     */
    private void updateSimulatedSteps() {
        // Simulate step increase with probability
        if (Math.random() > Constants.STEP_UPDATE_PROBABILITY) {
            stepCount += Constants.STEP_INCREMENT_BASE + random.nextInt(Constants.STEP_INCREMENT_RANGE);
            updateDisplay();
        }
    }

    /**
     * Update all UI displays
     */
    private void updateDisplay() {
        binding.stepsValue.setText(String.valueOf(stepCount));
        calculateStats();
    }

    /**
     * Calculate and display step statistics
     */
    private void calculateStats() {
        calculateDistance();
        calculateCalories();
        calculateActiveMinutes();
    }

    /**
     * Calculate distance walked
     */
    private void calculateDistance() {
        float distance = stepCount * Constants.STEP_LENGTH_KM;
        binding.distanceValue.setText(String.format(Constants.DISTANCE_FORMAT, distance));
    }

    /**
     * Calculate calories burned
     */
    private void calculateCalories() {
        int calories = (int) (stepCount * Constants.CALORIES_PER_STEP);
        binding.caloriesValue.setText(calories + Constants.SPACE + Constants.UNIT_CALORIES);
    }

    /**
     * Calculate active minutes
     */
    private void calculateActiveMinutes() {
        int activeMinutes = stepCount / Constants.STEPS_PER_MINUTE;
        binding.activeMinutesValue.setText(activeMinutes + Constants.SPACE + Constants.UNIT_MINUTES);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && event.values.length > Constants.POSITION_FIRST) {
            int newStepCount = (int) event.values[Constants.POSITION_FIRST];
            if (newStepCount > stepCount) {
                stepCount = newStepCount;
                runOnUiThread(this::updateDisplay);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes - no action needed for step counter
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