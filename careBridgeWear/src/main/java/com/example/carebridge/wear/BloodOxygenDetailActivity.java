package com.example.carebridge.wear;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.wear.databinding.ActivityBloodOxygenDetailBinding;

import java.util.Random;

public class BloodOxygenDetailActivity extends AppCompatActivity {

    private ActivityBloodOxygenDetailBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Random random = new Random();
    private int bloodOxygenLevel = 98;

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateSimulatedData();
            handler.postDelayed(this, 3000); // Update every 3 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBloodOxygenDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String oxygenValue = getIntent().getStringExtra("metric_value");
        if (oxygenValue != null) {
            try {
                bloodOxygenLevel = Integer.parseInt(oxygenValue);
            } catch (NumberFormatException e) {
                bloodOxygenLevel = 98;
            }
        }

        setupUI();
        updateDisplay();
        startUpdates();
    }

    private void setupUI() {
        binding.bloodOxygenBackButton.setOnClickListener(v -> finish());
        binding.bloodOxygenTitle.setText(getString(R.string.blood_oxygen_title));

        // Set normal range
        binding.normalRange.setText(getString(R.string.blood_oxygen_normal_range));

        // Set measurement tip
        binding.measurementTip.setText(getString(R.string.blood_oxygen_measurement_tip));
    }

    private void startUpdates() {
        handler.post(updateRunnable);
    }

    private void updateSimulatedData() {
        // Simulate small fluctuations
        int change = random.nextInt(3) - 1; // -1, 0, or 1
        bloodOxygenLevel = Math.max(95, Math.min(100, bloodOxygenLevel + change));

        runOnUiThread(this::updateDisplay);
    }

    private void updateDisplay() {
        binding.bloodOxygenValue.setText(String.valueOf(bloodOxygenLevel));

        // Update status
        if (bloodOxygenLevel >= 95) {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_normal));
            binding.oxygenStatus.setTextColor(getColor(R.color.green));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_normal_desc));
        } else if (bloodOxygenLevel >= 90) {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_low));
            binding.oxygenStatus.setTextColor(getColor(R.color.orange));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_low_desc));
        } else {
            binding.oxygenStatus.setText(getString(R.string.blood_oxygen_status_very_low));
            binding.oxygenStatus.setTextColor(getColor(R.color.red));
            binding.statusDescription.setText(getString(R.string.blood_oxygen_status_very_low_desc));
        }

        // Update graph
        updateGraph();
    }

    private void updateGraph() {
        binding.graphContainer.removeAllViews();

        int maxHeight = 80;
        int[] recentReadings = {95, 96, 97, 98, 97, 98, 99, 98, 97, bloodOxygenLevel};

        for (int i = 0; i < recentReadings.length; i++) {
            int reading = recentReadings[i];
            float percentage = (float) (reading - 90) / 10; // 90-100% range
            int height = (int) (maxHeight * percentage);

            androidx.appcompat.widget.AppCompatImageView bar = new androidx.appcompat.widget.AppCompatImageView(this);

            // Create gradient drawable for bar
            android.graphics.drawable.GradientDrawable gradient = new android.graphics.drawable.GradientDrawable();
            gradient.setColors(new int[]{
                    getColor(R.color.blue_500),
                    getColor(R.color.cyan)
            });
            gradient.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP);
            gradient.setCornerRadius(4f);
            bar.setBackground(gradient);

            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(
                            12, // width
                            height
                    );
            params.setMargins(2, maxHeight - height, 2, 0);

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