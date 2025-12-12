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
import androidx.wear.widget.WearableRecyclerView;

import com.example.carebridge.wear.adapters.HealthMetricAdapter;
import com.example.carebridge.wear.databinding.ActivityHealthMonitorBinding;
import com.example.carebridge.wear.models.HealthMetric;
import com.example.carebridge.wear.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthMonitorActivity extends AppCompatActivity implements
        SensorEventListener, HealthMetricAdapter.OnHealthMetricClickListener {

    private ActivityHealthMonitorBinding binding;
    private List<HealthMetric> healthMetrics = new ArrayList<>();
    private HealthMetricAdapter adapter;
    private int selectedPosition = Constants.POSITION_FIRST;

    // Sensor related
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor stepCounterSensor;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private boolean isMonitoring = false;

    // Update runnable
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateSimulatedData();
            handler.postDelayed(this, Constants.UPDATE_INTERVAL_MEDIUM);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupCarousel();
        checkPermissions();
    }

    /**
     * Set up UI components
     */
    private void setupUI() {
        binding.healthMonitorTitle.setText(getString(R.string.health_monitor_title));
    }

    /**
     * Set up carousel RecyclerView
     */
    private void setupCarousel() {
        // Initialize data with metrics - ONLY 3 ITEMS
        initializeHealthMetrics();

        // Create adapter
        adapter = new HealthMetricAdapter(healthMetrics, this);

        // Setup WearableLinearLayoutManager for normal vertical scrolling
        WearableLinearLayoutManager layoutManager = new WearableLinearLayoutManager(this);
        layoutManager.setOrientation(WearableLinearLayoutManager.VERTICAL);

        // Add scaling effect based on position from center
        layoutManager.setLayoutCallback(new WearableLinearLayoutManager.LayoutCallback() {
            @Override
            public void onLayoutFinished(View child, RecyclerView parent) {
                calculateAndApplyScaling(child, parent);
            }
        });

        // Set up the WearableRecyclerView
        binding.healthMonitorRecyclerView.setLayoutManager(layoutManager);
        binding.healthMonitorRecyclerView.setAdapter(adapter);
        binding.healthMonitorRecyclerView.setHasFixedSize(true);

        // DISABLE circular scrolling
        binding.healthMonitorRecyclerView.setCircularScrollingGestureEnabled(false);

        // DISABLE edge centering - this causes automatic scrolling
        binding.healthMonitorRecyclerView.setEdgeItemsCenteringEnabled(false);

        // Set up scroll listener
        binding.healthMonitorRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    handleScrollIdleState(recyclerView);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Update scaling during scroll
                for (int i = Constants.POSITION_FIRST; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    layoutManager.getLayoutCallback().onLayoutFinished(child, recyclerView);
                }
            }
        });

        // Initialize selection to first item (Heart Rate)
        adapter.setSelectedPosition(selectedPosition);
    }

    /**
     * Calculate and apply scaling based on distance from center
     */
    private void calculateAndApplyScaling(View child, RecyclerView parent) {
        float centerY = parent.getHeight() / Constants.FLOAT_DIVISOR_TWO;
        float childCenterY = child.getY() + child.getHeight() / Constants.FLOAT_DIVISOR_TWO;
        float distanceFromCenter = Math.abs(centerY - childCenterY);

        float maxDistance = centerY;
        if (maxDistance > Constants.POSITION_FIRST) {
            // Subtle scaling effect
            float scale = Constants.FLOAT_SCALE_BASE - (distanceFromCenter / maxDistance) * Constants.FLOAT_SCALE_FACTOR;
            scale = Math.max(Constants.FLOAT_SCALE_MIN, Math.min(Constants.FLOAT_SCALE_MAX, scale));

            float alpha = Constants.FLOAT_ALPHA_BASE - (distanceFromCenter / maxDistance) * Constants.FLOAT_ALPHA_FACTOR;
            alpha = Math.max(Constants.FLOAT_ALPHA_MIN, Math.min(Constants.FLOAT_ALPHA_MAX, alpha));

            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setAlpha(alpha);
        }
    }

    /**
     * Handle scroll idle state to update selection
     */
    private void handleScrollIdleState(RecyclerView recyclerView) {
        View closestToCenter = null;
        float minDistance = Float.MAX_VALUE;

        for (int i = Constants.POSITION_FIRST; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int position = recyclerView.getChildAdapterPosition(child);
            if (position != RecyclerView.NO_POSITION) {
                float centerY = recyclerView.getHeight() / Constants.FLOAT_DIVISOR_TWO;
                float childCenterY = child.getY() + child.getHeight() / Constants.FLOAT_DIVISOR_TWO;
                float distance = Math.abs(centerY - childCenterY);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestToCenter = child;
                }
            }
        }

        if (closestToCenter != null) {
            int position = recyclerView.getChildAdapterPosition(closestToCenter);
            if (position != RecyclerView.NO_POSITION) {
                updateSelection(position);
            }
        }
    }

    /**
     * Initialize health metrics data
     */
    private void initializeHealthMetrics() {
        healthMetrics.clear();

        // Exactly 3 metrics
        healthMetrics.add(new HealthMetric(
                Constants.METRIC_HEART_RATE,
                getString(R.string.heart_rate_label),
                String.valueOf(Constants.DEFAULT_HEART_RATE),
                getString(R.string.unit_bpm),
                getString(R.string.heart_rate_desc),
                R.drawable.ic_heart,
                R.color.red,
                R.color.heart_rate_start,
                R.color.heart_rate_end,
                Constants.CLASS_HEART_RATE_DETAIL
        ));

        healthMetrics.add(new HealthMetric(
                Constants.METRIC_STEPS,
                getString(R.string.steps_label),
                String.valueOf(Constants.DEFAULT_STEPS_VALUE),
                getString(R.string.unit_steps),
                getString(R.string.steps_desc),
                R.drawable.ic_steps,
                R.color.purple_500,
                R.color.steps_start,
                R.color.steps_end,
                Constants.CLASS_STEPS_DETAIL
        ));
    }

    /**
     * Update selection position
     */
    private void updateSelection(int newPosition) {
        selectedPosition = newPosition;

        // Update cursor position in adapter
        if (adapter != null) {
            adapter.setSelectedPosition(selectedPosition);
        }

        // Update cursor visibility
        binding.healthMonitorRecyclerView.post(() -> {
            for (int i = Constants.POSITION_FIRST; i < binding.healthMonitorRecyclerView.getChildCount(); i++) {
                View child = binding.healthMonitorRecyclerView.getChildAt(i);
                int position = binding.healthMonitorRecyclerView.getChildAdapterPosition(child);
                if (position != RecyclerView.NO_POSITION) {
                    View cursor = child.findViewById(R.id.selection_cursor);
                    if (cursor != null) {
                        boolean isSelected = (position == selectedPosition);
                        cursor.setVisibility(isSelected ? View.VISIBLE : View.GONE);

                        // Animate cursor appearance
                        if (isSelected) {
                            cursor.setAlpha(Constants.FLOAT_ALPHA_START);
                            cursor.animate()
                                    .alpha(Constants.FLOAT_ALPHA_END)
                                    .setDuration(Constants.ANIMATION_DURATION_SHORT)
                                    .start();
                        }
                    }
                }
            }
        });
    }

    /**
     * Check and request permissions
     */
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BODY_SENSORS)) {

                Snackbar.make(binding.getRoot(),
                                getString(R.string.health_monitoring_permission_msg),
                                Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.grant), v -> requestPermissions())
                        .show();
            } else {
                requestPermissions();
            }
        } else {
            startSensors();
        }
    }

    /**
     * Request permissions
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.ACTIVITY_RECOGNITION
                },
                Constants.PERMISSIONS_REQUEST_BODY_SENSORS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSIONS_REQUEST_BODY_SENSORS) {
            handlePermissionResult(grantResults);
        }
    }

    /**
     * Handle permission request result
     */
    private void handlePermissionResult(@NonNull int[] grantResults) {
        if (grantResults.length > Constants.POSITION_FIRST &&
                grantResults[Constants.POSITION_FIRST] == PackageManager.PERMISSION_GRANTED) {
            startSensors();
        } else {
            startSimulatedUpdates();
        }
    }

    /**
     * Start sensor monitoring
     */
    private void startSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Heart rate sensor
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            isMonitoring = true;
            Log.d(Constants.TAG_HEALTH_MONITOR_ACTIVITY, Constants.LOG_MSG_HEART_RATE_SENSOR_REGISTERED);
        } else {
            Log.w(Constants.TAG_HEALTH_MONITOR_ACTIVITY, Constants.LOG_MSG_NO_HEART_RATE_SENSOR);
        }

        // Step counter sensor
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(Constants.TAG_HEALTH_MONITOR_ACTIVITY, Constants.LOG_MSG_STEP_COUNTER_SENSOR_REGISTERED);
        } else {
            Log.w(Constants.TAG_HEALTH_MONITOR_ACTIVITY, Constants.LOG_MSG_NO_STEP_COUNTER_SENSOR);
        }

        startSimulatedUpdates();
    }

    /**
     * Start simulated data updates
     */
    private void startSimulatedUpdates() {
        handler.post(updateRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values.length > Constants.POSITION_FIRST) {
            float heartRate = event.values[Constants.POSITION_FIRST];
            updateHeartRate(heartRate);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && event.values.length > Constants.POSITION_FIRST) {
            float steps = event.values[Constants.POSITION_FIRST];
            updateSteps((int) steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(Constants.TAG_HEALTH_MONITOR_ACTIVITY,
                Constants.LOG_MSG_SENSOR_ACCURACY_CHANGED +
                        Constants.COLON + Constants.SPACE + sensor.getName() +
                        Constants.SPACE + Constants.LOG_MSG_ACCURACY + Constants.SPACE + accuracy);
    }

    /**
     * Update heart rate display
     */
    private void updateHeartRate(float heartRate) {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                if (metric.getId().equals(Constants.METRIC_HEART_RATE)) {
                    metric.setValue(String.valueOf((int) heartRate));
                    break;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Update steps display
     */
    private void updateSteps(int steps) {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                if (metric.getId().equals(Constants.METRIC_STEPS)) {
                    metric.setValue(String.valueOf(steps));
                    break;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Update simulated data
     */
    private void updateSimulatedData() {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                String metricId = metric.getId();

                if (metricId.equals(Constants.METRIC_HEART_RATE)) {
                    if (!isMonitoring) {
                        int heartRate = Constants.HEART_RATE_MIN + random.nextInt(Constants.HEART_RATE_RANGE);
                        metric.setValue(String.valueOf(heartRate));
                    }
                } else if (metricId.equals(Constants.METRIC_STEPS)) {
                    if (stepCounterSensor == null) {
                        int steps = Constants.STEPS_BASE_VALUE + random.nextInt(Constants.STEPS_RANGE);
                        metric.setValue(String.valueOf(steps));
                    }
                } else if (metricId.equals(Constants.METRIC_BLOOD_OXYGEN)) {
                    int bloodOxygen = Constants.BLOOD_OXYGEN_MIN + random.nextInt(Constants.BLOOD_OXYGEN_RANGE);
                    metric.setValue(String.valueOf(bloodOxygen));
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHealthMetricClick(HealthMetric metric) {
        Class<?> activityClass = null;

        if (metric.getId().equals(Constants.METRIC_HEART_RATE)) {
            activityClass = HeartRateDetailActivity.class;
        } else if (metric.getId().equals(Constants.METRIC_STEPS)) {
            activityClass = StepsDetailActivity.class;
        } else if (metric.getId().equals(Constants.METRIC_BLOOD_OXYGEN)) {
            activityClass = BloodOxygenDetailActivity.class;
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            intent.putExtra(Constants.EXTRA_METRIC_VALUE, metric.getValue());
            intent.putExtra(Constants.EXTRA_METRIC_LABEL, metric.getLabel());
            startActivity(intent);
        }
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
        if (sensorManager != null) {
            if (heartRateSensor != null) {
                sensorManager.registerListener(this, heartRateSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        handler.post(updateRunnable);
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