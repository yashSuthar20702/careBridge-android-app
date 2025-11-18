package com.example.carebridge.wear.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.carebridge.wear.R;

public class MedicineFragment extends BaseFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_medicine;
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
        LinearLayout medicineList = view.findViewById(R.id.medicine_list);
        medicineList.removeAllViews();

        int[] medicineResources = {
                R.string.medicine_paracetamol,
                R.string.medicine_vitamin_d,
                R.string.medicine_blood_pressure
        };

        for (int medicineRes : medicineResources) {
            TextView medText = new TextView(getContext());
            medText.setText(getString(medicineRes));
            medText.setTextColor(getResources().getColor(R.color.text_primary));
            medText.setTextSize(12);
            medText.setPadding(0, 8, 0, 8);
            medText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            medicineList.addView(medText);
        }
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