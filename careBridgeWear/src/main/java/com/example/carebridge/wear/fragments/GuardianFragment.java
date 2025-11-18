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

public class GuardianFragment extends BaseFragment {

    private String guardianName;
    private String guardianPhone;
    private String guardianRelationship;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_guardian;
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
        guardianName = getString(R.string.guardian_name);
        guardianPhone = getString(R.string.guardian_phone);
        guardianRelationship = getString(R.string.guardian_relationship);
    }

    @Override
    protected void initializeViews(View view) {
        TextView tvGuardianName = view.findViewById(R.id.tv_guardian_name);
        TextView tvGuardianPhone = view.findViewById(R.id.tv_guardian_phone);
        TextView tvGuardianRelationship = view.findViewById(R.id.tv_guardian_relationship);

        tvGuardianName.setText(getString(R.string.name_label, guardianName));
        tvGuardianPhone.setText(getString(R.string.phone_label, guardianPhone));
        tvGuardianRelationship.setText(getString(R.string.relationship_label, guardianRelationship));

        ImageButton btnCallGuardian = view.findViewById(R.id.btn_call_guardian);
        btnCallGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + guardianPhone));
                startActivity(intent);
            }
        });
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