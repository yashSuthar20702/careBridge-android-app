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

import java.util.Locale;
import java.util.Random;

/**
 * StepsDetailActivity

 * Displays step tracking details on Wear OS.
 * Uses the Step Counter sensor when available.
 * Falls back to simulated data if the sensor is not supported.
 */
public class StepsDetailActivity extends AppCompatActivity
        implements SensorEventListener {

    private ActivityStepsDetailBinding binding;

    // Sensor related objects
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    // Step tracking value
    private int stepCount = Constants.POSITION_FIRST;

    // Handler for simulated updates
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    /**
     * Runnable used to simulate step updates when sensor is unavailable
     */
    private final Runnable updateRunnable = new Runnable() {
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

        String stepsValue =
                getIntent().getStringExtra(Constants.EXTRA_METRIC_VALUE);
        initializeStepCount(stepsValue);

        setupUI();
        setupSensor();
        updateDisplay();
    }

    /**
     * Initializes step count from intent or fallback value
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
     * Sets static UI values
     */
    private void setupUI() {
        binding.stepsTitle.setText(
                getString(R.string.steps_tracker_title)
        );
    }

    /**
     * Configures step counter sensor
     */
    private void setupSensor() {
        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        stepCounterSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor != null) {
            sensorManager.registerListener(
                    this,
                    stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        } else {
            handler.post(updateRunnable);
        }
    }

    /**
     * Generates simulated step data
     */
    private void updateSimulatedSteps() {
        if (Math.random() > Constants.STEP_UPDATE_PROBABILITY) {
            stepCount += Constants.STEP_INCREMENT_BASE
                    + random.nextInt(Constants.STEP_INCREMENT_RANGE);
            updateDisplay();
        }
    }

    /**
     * Updates all step-related UI values
     */
    private void updateDisplay() {
        binding.stepsValue.setText(
                String.valueOf(stepCount)
        );
        calculateStats();
    }

    /**
     * Calculates and updates step statistics
     */
    private void calculateStats() {
        calculateDistance();
        calculateCalories();
        calculateActiveMinutes();
    }

    /**
     * Calculates distance walked
     */
    private void calculateDistance() {
        float distance = stepCount * Constants.STEP_LENGTH_KM;

        binding.distanceValue.setText(
                getString(
                        R.string.steps_distance_value,
                        Float.valueOf(
                                String.format(
                                        Locale.getDefault(),
                                        Constants.DISTANCE_FORMAT,
                                        distance
                                )
                        )
                )
        );
    }

    /**
     * Calculates calories burned
     */
    private void calculateCalories() {
        int calories =
                (int) (stepCount * Constants.CALORIES_PER_STEP);

        binding.caloriesValue.setText(
                getString(
                        R.string.steps_calories_value,
                        calories
                )
        );
    }

    /**
     * Calculates active minutes
     */
    private void calculateActiveMinutes() {
        int activeMinutes =
                stepCount / Constants.STEPS_PER_MINUTE;

        binding.activeMinutesValue.setText(
                getString(
                        R.string.steps_active_minutes_value,
                        activeMinutes
                )
        );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER
                && event.values.length > Constants.POSITION_FIRST) {

            int newStepCount =
                    (int) event.values[Constants.POSITION_FIRST];

            if (newStepCount > stepCount) {
                stepCount = newStepCount;
                runOnUiThread(this::updateDisplay);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action required for step counter accuracy changes
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
            sensorManager.registerListener(
                    this,
                    stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
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