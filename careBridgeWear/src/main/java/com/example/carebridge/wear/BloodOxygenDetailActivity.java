package com.example.carebridge.wear;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.carebridge.wear.databinding.ActivityBloodOxygenDetailBinding;

import java.util.Random;

public class BloodOxygenDetailActivity extends AppCompatActivity {

    private ActivityBloodOxygenDetailBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();

    private int bloodOxygenLevel;

    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBloodOxygenDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bloodOxygenLevel = getResources().getInteger(R.integer.oxygen_default_level);

        String oxygenValue = getIntent().getStringExtra("metric_value");
        if (oxygenValue != null) {
            try {
                bloodOxygenLevel = Integer.parseInt(oxygenValue);
            } catch (NumberFormatException ignored) {
                bloodOxygenLevel = getResources().getInteger(R.integer.oxygen_default_level);
            }
        }

        setupUI();
        updateDisplay();
        startUpdates();
    }

    private void setupUI() {
        binding.bloodOxygenTitle.setText(getString(R.string.blood_oxygen_title));
        binding.normalRange.setText(getString(R.string.blood_oxygen_normal_range));
        binding.measurementTip.setText(getString(R.string.blood_oxygen_measurement_tip));
    }

    private void startUpdates() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateSimulatedData();
                handler.postDelayed(
                        this,
                        getResources().getInteger(R.integer.oxygen_update_interval_ms)
                );
            }
        };
        handler.post(updateRunnable);
    }

    private void updateSimulatedData() {
        int randomRange = getResources().getInteger(R.integer.oxygen_random_range);
        int change = random.nextInt(randomRange) - 1;

        int min = getResources().getInteger(R.integer.oxygen_normal_min);
        int max = getResources().getInteger(R.integer.oxygen_max);

        bloodOxygenLevel = Math.max(min, Math.min(max, bloodOxygenLevel + change));
        runOnUiThread(this::updateDisplay);
    }

    private void updateDisplay() {
        binding.bloodOxygenValue.setText(String.valueOf(bloodOxygenLevel));

        int normalMin = getResources().getInteger(R.integer.oxygen_normal_min);
        int lowMin = getResources().getInteger(R.integer.oxygen_low_min);

        if (bloodOxygenLevel >= normalMin) {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_normal));
            binding.oxygenStatus.setTextColor(getColor(R.color.green));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_normal_desc));
        } else if (bloodOxygenLevel >= lowMin) {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_low));
            binding.oxygenStatus.setTextColor(getColor(R.color.orange));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_low_desc));
        } else {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_very_low));
            binding.oxygenStatus.setTextColor(getColor(R.color.red));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_very_low_desc));
        }

        updateGraph();
    }

    private void updateGraph() {
        binding.graphContainer.removeAllViews();

        int maxHeight = getResources().getInteger(R.integer.oxygen_graph_max_height);
        int barWidth = getResources().getInteger(R.integer.oxygen_graph_bar_width);
        int barMargin = getResources().getInteger(R.integer.oxygen_graph_bar_margin);
        float cornerRadius = getResources().getDimension(R.dimen.oxygen_graph_bar_corner_radius);

        int[] recentReadings = {
                95, 96, 97, 98, 97, 98, 99, 98, 97, bloodOxygenLevel
        };

        for (int reading : recentReadings) {
            float percentage = (float) (reading - 90) / 10;
            int height = (int) (maxHeight * percentage);

            AppCompatImageView bar = new AppCompatImageView(this);

            android.graphics.drawable.GradientDrawable gradient =
                    new android.graphics.drawable.GradientDrawable();
            gradient.setColors(new int[]{
                    getColor(R.color.blue_500),
                    getColor(R.color.cyan)
            });
            gradient.setOrientation(
                    android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
            );
            gradient.setCornerRadius(cornerRadius);

            bar.setBackground(gradient);

            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(barWidth, height);
            params.setMargins(barMargin, maxHeight - height, barMargin, 0);

            binding.graphContainer.addView(bar, params);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}