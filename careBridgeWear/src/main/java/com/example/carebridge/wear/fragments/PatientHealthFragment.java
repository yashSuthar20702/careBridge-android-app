package com.example.carebridge.wear.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carebridge.wear.R;

public class PatientHealthFragment extends BaseFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_patient_health;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initializeViews(view);
        setupSwipeGestures(view);
        return view;
    }

    @Override
    protected void initializeViews(View view) {
        TextView tvHeartRate = view.findViewById(R.id.tv_heart_rate);
        TextView tvBloodPressure = view.findViewById(R.id.tv_blood_pressure);
        TextView tvLastCheck = view.findViewById(R.id.tv_last_check);

        tvHeartRate.setText(getString(R.string.heart_rate));
        tvBloodPressure.setText(getString(R.string.blood_pressure));
        tvLastCheck.setText(getString(R.string.last_check));
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