package com.example.carebridge.wear.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carebridge.wear.R;
import com.example.carebridge.wear.adapters.HomePagerAdapter;
import com.example.carebridge.wear.databinding.FragmentHomeBinding;
import com.example.carebridge.wear.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler timeHandler;
    private Runnable timeRunnable;
    private View[] indicators;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initIndicators();
        setupViewPager();
        startTimeUpdates();
    }

    /**
     * Initialize page indicators
     */
    private void initIndicators() {
        indicators = new View[]{
                binding.homeIndicator0,
                binding.homeIndicator1,
                binding.homeIndicator2,
                binding.homeIndicator3,
                binding.homeIndicator4,
                binding.homeIndicator5
        };

        for (int i = Constants.POSITION_FIRST; i < indicators.length; i++) {
            int index = i;
            indicators[i].setOnClickListener(v -> binding.viewPager.setCurrentItem(index, true));
        }
    }

    /**
     * Set up ViewPager with adapter and page change listener
     */
    private void setupViewPager() {
        HomePagerAdapter adapter = new HomePagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });

        updateIndicators(Constants.POSITION_FIRST);
    }

    /**
     * Update indicator appearance based on active position
     */
    private void updateIndicators(int activePos) {
        for (int i = Constants.POSITION_FIRST; i < indicators.length; i++) {
            View indicator = indicators[i];

            if (i == activePos) {
                indicator.setBackgroundResource(R.drawable.indicator_active);
                animateActive(indicator);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive);
                animateInactive(indicator);
            }

            animateMargin(indicator, i == activePos);
        }
    }

    /**
     * Animate active indicator with scale up
     */
    private void animateActive(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,
                Constants.FLOAT_SCALE_INACTIVE, Constants.FLOAT_SCALE_ACTIVE);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,
                Constants.FLOAT_SCALE_INACTIVE, Constants.FLOAT_SCALE_ACTIVE);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        anim.setDuration(Constants.ANIMATION_DURATION_MEDIUM);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    /**
     * Animate inactive indicator with scale down
     */
    private void animateInactive(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,
                Constants.FLOAT_SCALE_ACTIVE, Constants.FLOAT_SCALE_INACTIVE);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,
                Constants.FLOAT_SCALE_ACTIVE, Constants.FLOAT_SCALE_INACTIVE);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        anim.setDuration(Constants.ANIMATION_DURATION_SHORT);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    /**
     * Animate margin changes for indicators
     */
    private void animateMargin(View view, boolean isActive) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int start = params.leftMargin;
        int end = isActive ? Constants.INDICATOR_MARGIN_ACTIVE : Constants.INDICATOR_MARGIN_INACTIVE;

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(Constants.ANIMATION_DURATION_MEDIUM);
        animator.addUpdateListener(animation -> {
            int marginValue = (int) animation.getAnimatedValue();
            params.leftMargin = marginValue;
            params.rightMargin = marginValue;
            view.setLayoutParams(params);
        });

        animator.start();
    }

    /**
     * Start time update handler
     */
    private void startTimeUpdates() {
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, Constants.UPDATE_INTERVAL_FAST);
            }
        };
        timeHandler.post(timeRunnable);
    }

    /**
     * Update time display
     */
    private void updateTime() {
        if (binding == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT_HH_MM, Locale.getDefault());
        binding.homeTime.setText(sdf.format(new Date()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timeHandler != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        binding = null;
    }
}