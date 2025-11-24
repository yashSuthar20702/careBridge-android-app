package com.example.carebridge.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carebridge.wear.databinding.ActivityCallBinding;

public class CallActivity extends AppCompatActivity {

    private ActivityCallBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.callBackButton.setOnClickListener(v -> finish());

        binding.callEmergencyContact.setOnClickListener(v -> makeCall("911"));
        binding.callGuardianContact.setOnClickListener(v -> makeCall("+1 519 573 0317"));
    }

    private void makeCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}