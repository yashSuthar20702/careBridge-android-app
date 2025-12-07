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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HealthMonitorActivity extends AppCompatActivity implements
        SensorEventListener, HealthMetricAdapter.OnHealthMetricClickListener {

    private static final String TAG = "HealthMonitorActivity";
    private static final int PERMISSIONS_REQUEST = 100;

    private ActivityHealthMonitorBinding binding;
    private List<HealthMetric> healthMetrics = new ArrayList<>();
    private HealthMetricAdapter adapter;
    private int selectedPosition = 0;

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
            handler.postDelayed(this, 3000);
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

    private void setupUI() {
        binding.healthMonitorBackButton.setOnClickListener(v -> finish());
        binding.healthMonitorTitle.setText(getString(R.string.health_monitor_title));
    }

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
                // Calculate distance from center for subtle scaling
                float centerY = parent.getHeight() / 2f;
                float childCenterY = child.getY() + child.getHeight() / 2f;
                float distanceFromCenter = Math.abs(centerY - childCenterY);

                float maxDistance = centerY;
                if (maxDistance > 0) {
                    // Subtle scaling effect
                    float scale = 1.0f - (distanceFromCenter / maxDistance) * 0.15f;
                    scale = Math.max(0.85f, Math.min(1.1f, scale));

                    float alpha = 1.0f - (distanceFromCenter / maxDistance) * 0.3f;
                    alpha = Math.max(0.7f, Math.min(1.0f, alpha));

                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    child.setAlpha(alpha);
                }
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

        // Simple scroll listener to update scaling
        binding.healthMonitorRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Find which item is closest to center
                    View closestToCenter = null;
                    float minDistance = Float.MAX_VALUE;

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        int position = recyclerView.getChildAdapterPosition(child);
                        if (position != RecyclerView.NO_POSITION) {
                            float centerY = recyclerView.getHeight() / 2f;
                            float childCenterY = child.getY() + child.getHeight() / 2f;
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
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Update scaling during scroll
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    layoutManager.getLayoutCallback().onLayoutFinished(child, recyclerView);
                }
            }
        });

        // Initialize selection to first item (Heart Rate)
        selectedPosition = 0;
        adapter.setSelectedPosition(selectedPosition);

        // NO automatic scrolling - let the RecyclerView show items naturally
    }

    private void initializeHealthMetrics() {
        healthMetrics.clear();

        // Exactly 3 metrics
        healthMetrics.add(new HealthMetric(
                "heart_rate",
                getString(R.string.heart_rate_label),
                "72",
                getString(R.string.unit_bpm),
                getString(R.string.heart_rate_desc),
                R.drawable.ic_heart,
                R.color.red,
                R.color.heart_rate_start,
                R.color.heart_rate_end,
                "HeartRateDetailActivity"
        ));

        healthMetrics.add(new HealthMetric(
                "steps",
                getString(R.string.steps_label),
                "3542",
                getString(R.string.unit_steps),
                getString(R.string.steps_desc),
                R.drawable.ic_steps,
                R.color.purple_500,
                R.color.steps_start,
                R.color.steps_end,
                "StepsDetailActivity"
        ));

        healthMetrics.add(new HealthMetric(
                "blood_oxygen",
                getString(R.string.blood_oxygen_label),
                "98",
                getString(R.string.unit_percent),
                getString(R.string.blood_oxygen_desc),
                R.drawable.ic_droplet,
                R.color.blue_500,
                R.color.blood_oxygen_start,
                R.color.blood_oxygen_end,
                "BloodOxygenDetailActivity"
        ));
    }

    private void updateSelection(int newPosition) {
        int oldPosition = selectedPosition;
        selectedPosition = newPosition;

        // Update cursor position in adapter
        if (adapter != null) {
            adapter.setSelectedPosition(selectedPosition);
        }

        // Update cursor visibility
        binding.healthMonitorRecyclerView.post(() -> {
            for (int i = 0; i < binding.healthMonitorRecyclerView.getChildCount(); i++) {
                View child = binding.healthMonitorRecyclerView.getChildAt(i);
                int position = binding.healthMonitorRecyclerView.getChildAdapterPosition(child);
                if (position != RecyclerView.NO_POSITION) {
                    View cursor = child.findViewById(R.id.selection_cursor);
                    if (cursor != null) {
                        boolean isSelected = (position == selectedPosition);
                        cursor.setVisibility(isSelected ? View.VISIBLE : View.GONE);

                        // Animate cursor appearance
                        if (isSelected) {
                            cursor.setAlpha(0f);
                            cursor.animate()
                                    .alpha(1f)
                                    .setDuration(200)
                                    .start();
                        }
                    }
                }
            }
        });
    }

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

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.BODY_SENSORS,
                        Manifest.permission.ACTIVITY_RECOGNITION
                },
                PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSensors();
            } else {
                startSimulatedUpdates();
            }
        }
    }

    private void startSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Heart rate sensor
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            isMonitoring = true;
            Log.d(TAG, "Heart rate sensor registered");
        } else {
            Log.w(TAG, "No heart rate sensor available");
        }

        // Step counter sensor
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Step counter sensor registered");
        } else {
            Log.w(TAG, "No step counter sensor available");
        }

        startSimulatedUpdates();
    }

    private void startSimulatedUpdates() {
        handler.post(updateRunnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE && event.values.length > 0) {
            float heartRate = event.values[0];
            updateHeartRate(heartRate);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && event.values.length > 0) {
            float steps = event.values[0];
            updateSteps((int) steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Sensor accuracy changed: " + sensor.getName() + " accuracy: " + accuracy);
    }

    private void updateHeartRate(float heartRate) {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                if (metric.getId().equals("heart_rate")) {
                    metric.setValue(String.valueOf((int) heartRate));
                    break;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateSteps(int steps) {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                if (metric.getId().equals("steps")) {
                    metric.setValue(String.valueOf(steps));
                    break;
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateSimulatedData() {
        runOnUiThread(() -> {
            for (HealthMetric metric : healthMetrics) {
                String metricId = metric.getId();

                if (metricId.equals("heart_rate")) {
                    if (!isMonitoring) {
                        int heartRate = 65 + random.nextInt(20);
                        metric.setValue(String.valueOf(heartRate));
                    }
                } else if (metricId.equals("steps")) {
                    if (stepCounterSensor == null) {
                        int steps = 3500 + random.nextInt(500);
                        metric.setValue(String.valueOf(steps));
                    }
                } else if (metricId.equals("blood_oxygen")) {
                    int bloodOxygen = 96 + random.nextInt(4);
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

        switch (metric.getId()) {
            case "heart_rate":
                activityClass = HeartRateDetailActivity.class;
                break;
            case "steps":
                activityClass = StepsDetailActivity.class;
                break;
            case "blood_oxygen":
                activityClass = BloodOxygenDetailActivity.class;
                break;
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            intent.putExtra("metric_value", metric.getValue());
            intent.putExtra("metric_label", metric.getLabel());
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