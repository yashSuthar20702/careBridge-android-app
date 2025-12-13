package com.example.carebridge.wear;

import android.Manifest;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;

import com.example.carebridge.wear.adapters.HealthMetricAdapter;
import com.example.carebridge.wear.databinding.ActivityHealthMonitorBinding;
import com.example.carebridge.wear.models.HealthMetric;
import com.example.carebridge.wear.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HealthMonitorActivity
 *
 * This activity displays real-time and simulated health metrics
 * such as Heart Rate, Steps, and Blood Oxygen on a Wear OS device.
 *
 * Features:
 * - Uses WearableRecyclerView for smooth scrolling on small screens
 * - Supports real sensor data when permissions are granted
 * - Falls back to simulated data if sensors are unavailable
 * - Uses ViewBinding (no findViewById)
 */
public class HealthMonitorActivity extends AppCompatActivity
        implements SensorEventListener, HealthMetricAdapter.OnHealthMetricClickListener {

    // ViewBinding reference for accessing UI elements safely
    private ActivityHealthMonitorBinding binding;

    // List holding all health metric cards displayed on the screen
    private final List<HealthMetric> healthMetrics = new ArrayList<>();

    // RecyclerView adapter for health metrics
    private HealthMetricAdapter adapter;

    // Tracks currently selected metric in the carousel
    private int selectedPosition = Constants.POSITION_FIRST;

    // Android sensor framework objects
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor stepCounterSensor;

    // Handler used to update simulated data periodically
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Random generator for simulated values
    private final Random random = new Random();

    // Flag to indicate whether real sensors are active
    private boolean isMonitoring = false;

    // Runnable that updates simulated data at a fixed interval
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateSimulatedData();
            handler.postDelayed(this, Constants.UPDATE_INTERVAL_MEDIUM);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityHealthMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize UI and logic
        setupUI();
        setupCarousel();
        checkPermissions();
    }

    /**
     * Sets static UI elements such as the screen title
     */
    private void setupUI() {
        binding.healthMonitorTitle.setText(getString(R.string.health_monitor_title));
    }

    /**
     * Sets up the WearableRecyclerView with scaling effect
     * and scroll behavior optimized for Wear OS
     */
    private void setupCarousel() {
        initializeHealthMetrics();

        adapter = new HealthMetricAdapter(healthMetrics, this);

        // Wear-specific layout manager for better scrolling on watches
        WearableLinearLayoutManager layoutManager =
                new WearableLinearLayoutManager(this);

        // Apply scaling effect to items based on distance from center
        layoutManager.setLayoutCallback(
                new WearableLinearLayoutManager.LayoutCallback() {
                    @Override
                    public void onLayoutFinished(View child, RecyclerView parent) {
                        applyScaling(child, parent);
                    }
                }
        );

        binding.healthMonitorRecyclerView.setLayoutManager(layoutManager);
        binding.healthMonitorRecyclerView.setAdapter(adapter);
        binding.healthMonitorRecyclerView.setHasFixedSize(true);

        // Disable circular scrolling to avoid unwanted auto-scroll
        binding.healthMonitorRecyclerView.setCircularScrollingGestureEnabled(false);
        binding.healthMonitorRecyclerView.setEdgeItemsCenteringEnabled(false);

        // Detect when scrolling stops to update selected item
        binding.healthMonitorRecyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            @NonNull RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            updateSelectionOnIdle(recyclerView);
                        }
                    }
                });

        // Set initial selection
        adapter.setSelectedPosition(selectedPosition);
    }

    /**
     * Applies a scaling effect to RecyclerView items
     * Items closer to the center appear larger
     */
    private void applyScaling(View child, RecyclerView parent) {
        float centerY = parent.getHeight() / 2f;
        float childCenterY = child.getY() + child.getHeight() / 2f;
        float distance = Math.abs(centerY - childCenterY);

        float scale = 1.0f - (distance / centerY) * 0.15f;
        scale = Math.max(0.85f, scale);

        child.setScaleX(scale);
        child.setScaleY(scale);
    }

    /**
     * Determines which item is closest to the center after scrolling stops
     */
    private void updateSelectionOnIdle(RecyclerView recyclerView) {
        View closest = null;
        float minDistance = Float.MAX_VALUE;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            float centerY = recyclerView.getHeight() / 2f;
            float childCenterY = child.getY() + child.getHeight() / 2f;
            float distance = Math.abs(centerY - childCenterY);

            if (distance < minDistance) {
                minDistance = distance;
                closest = child;
            }
        }

        if (closest != null) {
            int pos = recyclerView.getChildAdapterPosition(closest);
            if (pos != RecyclerView.NO_POSITION) {
                selectedPosition = pos;
                adapter.setSelectedPosition(pos);
            }
        }
    }

    /**
     * Initializes the list of health metrics
     * All values, colors, and strings come from resources/constants
     */
    private void initializeHealthMetrics() {
        healthMetrics.clear();

        healthMetrics.add(new HealthMetric(
                Constants.METRIC_HEART_RATE,
                getString(R.string.heart_rate_label),
                String.valueOf(Constants.DEFAULT_HEART_RATE),
                getString(R.string.unit_bpm),
                getString(R.string.heart_rate_desc),
                R.drawable.ic_heart,
                R.color.heart_rate_primary,
                R.color.heart_rate_start,
                R.color.heart_rate_end,
                HeartRateDetailActivity.class
        ));

        healthMetrics.add(new HealthMetric(
                Constants.METRIC_STEPS,
                getString(R.string.steps_label),
                String.valueOf(Constants.DEFAULT_STEPS_VALUE),
                getString(R.string.unit_steps),
                getString(R.string.steps_desc),
                R.drawable.ic_steps,
                R.color.steps_primary,
                R.color.steps_start,
                R.color.steps_end,
                StepsDetailActivity.class
        ));


    }

    /**
     * Checks runtime permissions for body sensors
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(binding.getRoot(),
                            getString(R.string.health_monitoring_permission_msg),
                            Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.grant), v -> requestPermissions())
                    .show();
        } else {
            startSensors();
        }
    }

    /**
     * Requests sensor-related permissions from the user
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.ACTIVITY_RECOGNITION
                },
                Constants.PERMISSIONS_REQUEST_BODY_SENSORS);
    }

    /**
     * Starts listening to available sensors
     */
    private void startSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            isMonitoring = true;
        }

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        handler.post(updateRunnable);
    }

    /**
     * Receives real sensor updates
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            updateMetric(Constants.METRIC_HEART_RATE, (int) event.values[0]);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            updateMetric(Constants.METRIC_STEPS, (int) event.values[0]);
        }
    }

    /**
     * Called when sensor accuracy changes
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(Constants.TAG_HEALTH_MONITOR_ACTIVITY,
                "Sensor accuracy changed: " + accuracy);
    }

    /**
     * Updates a metric value and refreshes the UI
     */
    private void updateMetric(String id, int value) {
        for (HealthMetric metric : healthMetrics) {
            if (metric.getId().equals(id)) {
                metric.setValue(String.valueOf(value));
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Generates simulated data when sensors are unavailable
     */
    private void updateSimulatedData() {
        if (!isMonitoring) {
            updateMetric(
                    Constants.METRIC_HEART_RATE,
                    Constants.HEART_RATE_MIN + random.nextInt(Constants.HEART_RATE_RANGE)
            );
        }
    }

    /**
     * Handles navigation when a metric card is tapped
     */
    @Override
    public void onHealthMetricClick(HealthMetric metric) {
        Intent intent = new Intent(this, metric.getDetailActivity());
        intent.putExtra(Constants.EXTRA_METRIC_VALUE, metric.getValue());
        intent.putExtra(Constants.EXTRA_METRIC_LABEL, metric.getLabel());
        startActivity(intent);
    }

    /**
     * Stops sensor updates when activity goes to background
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(updateRunnable);
    }

    /**
     * Resumes simulated updates when activity returns to foreground
     */
    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateRunnable);
    }
}