package com.example.carebridge.wear.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carebridge.wear.R;

public class CallFragment extends BaseFragment {

    private String emergencyNumber;
    private String guardianName;
    private String guardianPhone;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_call;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initializeData();
        initializeViews(view);
        setupSwipeGestures(view);
        return view;
    }

    private void initializeData() {
        emergencyNumber = getString(R.string.emergency_number);
        guardianName = getString(R.string.guardian_name);
        guardianPhone = getString(R.string.guardian_phone);
    }

    @Override
    protected void initializeViews(View view) {
        // Setup emergency call button
        ImageButton btnEmergencyCall = view.findViewById(R.id.btn_emergency_call);
        btnEmergencyCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(intent);
        });

        // Setup guardian call button
        ImageButton btnGuardianCall = view.findViewById(R.id.btn_guardian_call);
        btnGuardianCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + guardianPhone));
            startActivity(intent);
        });

        // Set the text views
        TextView tvEmergencyNumber = view.findViewById(R.id.tv_emergency_number);
        TextView tvGuardianName = view.findViewById(R.id.tv_guardian_name);

        tvEmergencyNumber.setText(emergencyNumber);
        tvGuardianName.setText(guardianName.split(" ")[0]);
    }

    @Override
    protected void setupSwipeGestures(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private static final int SWIPE_THRESHOLD = 50;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float diffX = endX - startX;

                        // Any horizontal swipe goes back to home
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && listener != null) {
                            listener.onBackToHome();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }
}