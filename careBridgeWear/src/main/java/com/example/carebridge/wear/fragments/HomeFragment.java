package com.example.carebridge.wear.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carebridge.wear.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends BaseFragment {

    private ImageButton btnMainAction;
    private TextView tvButtonLabel, tvClock;
    private LinearLayout pageIndicators;
    private Handler clockHandler;
    private Runnable clockRunnable;

    private int currentPage = 0;
    private static final int TOTAL_PAGES = 4;

    private int[] buttonIcons = {
            R.drawable.ic_call,
            R.drawable.ic_medicine,
            R.drawable.ic_health,
            R.drawable.ic_guardian
    };

    private int[] buttonLabels = {
            R.string.emergency_call_label,
            R.string.medicines_label,
            R.string.health_data_label,
            R.string.guardian_info_label
    };

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initializeViews(view);
        setupSwipeGestures(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startClock();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopClock();
    }

    @Override
    protected void initializeViews(View view) {
        btnMainAction = view.findViewById(R.id.btn_main_action);
        tvButtonLabel = view.findViewById(R.id.tv_button_label);
        tvClock = view.findViewById(R.id.tv_clock);
        pageIndicators = view.findViewById(R.id.page_indicators);

        // Initialize clock immediately
        updateClock();

        updateUI();
        setupPageIndicators();

        btnMainAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    navigateToScreen(currentPage);
                }
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

                        if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                            if (diffX > 0) {
                                // Swipe right - previous page
                                showPreviousPage();
                            } else {
                                // Swipe left - next page
                                showNextPage();
                            }
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void updateUI() {
        if (currentPage >= 0 && currentPage < buttonIcons.length) {
            btnMainAction.setImageResource(buttonIcons[currentPage]);
            tvButtonLabel.setText(getString(buttonLabels[currentPage]));
        }
    }

    private void setupPageIndicators() {
        if (pageIndicators == null) return;
        pageIndicators.removeAllViews();

        for (int i = 0; i < TOTAL_PAGES; i++) {
            View indicator = new View(getActivity());
            int size = i == currentPage ? 18 : 12;
            int margin = 6;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            indicator.setLayoutParams(params);

            // Set different background for active/inactive indicators
            if (i == currentPage) {
                indicator.setBackgroundResource(R.drawable.circle_indicator_active);
            } else {
                indicator.setBackgroundResource(R.drawable.circle_indicator_inactive);
            }

            final int pageIndex = i;
            indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPage = pageIndex;
                    updateUI();
                    setupPageIndicators();
                }
            });

            pageIndicators.addView(indicator);
        }
    }

    private void showPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateUI();
            setupPageIndicators();
        }
    }

    private void showNextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            currentPage++;
            updateUI();
            setupPageIndicators();
        }
    }

    private void navigateToScreen(int page) {
        if (listener != null) {
            listener.onNavigateToScreen(page);
        }
    }

    private void startClock() {
        // Stop any existing clock first
        stopClock();

        clockHandler = new Handler();
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                // Update every second
                clockHandler.postDelayed(this, 1000);
            }
        };
        // Start immediately
        clockHandler.post(clockRunnable);
    }

    private void stopClock() {
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
    }

    private void updateClock() {
        if (getActivity() != null && tvClock != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            // Run on UI thread to ensure thread safety
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvClock.setText(currentTime);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopClock();
    }
}